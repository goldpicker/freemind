<?PHP
include_once ('../../inc/inc.init_external.php');
ob_start();
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT"); // Datum aus Vergangenheit
header('Last-Modified: '.gmdate('D, d M Y H:i:s').' GMT'); // immer geändert
header("Cache-Control: no-store, no-cache, must-revalidate"); // HTTP/1.1
header("Cache-Control: post-check=0, pre-check=0", false);
header("Pragma: no-cache");

function fasttree_level_order($node_id = '0', $level = '0') {
        global $tlo_tree, $catlist, $catlist_level;
        for ($i=1; !empty($tlo_tree[$node_id][$i]); $i++) {
		$catlist[] = $tlo_tree[$node_id][$i];
		$catlist_level[$tlo_tree[$node_id][$i]] = $level;
		fasttree_level_order($tlo_tree[$node_id][$i], ($level + 1));
        }
        return;
}

if ($GetFiles == 'true') {
	$sql = "SELECT A.name, B.idcat, B.parent, B.sortindex FROM $dedi_db[cat_lang] AS A, $dedi_db[cat] AS B WHERE A.idcat=B.idcat AND B.idclient='$client' ORDER BY B.parent, B.sortindex";
	$db->query($sql);
	while ($db->next_record()){
		$ordner[$db->f('idcat')] = $db->f('name');
		$tlo_tree[$db->f('parent')][$db->f('sortindex')] = $db->f('idcat');
	}
	fasttree_level_order();
	$sql = "SELECT D.idcatside, D.idcat, D.sortindex, D.is_start, F.title FROM $dedi_db[cat_side] AS D, $dedi_db[side] AS E, $dedi_db[side_lang] AS F WHERE D.idside=E.idside AND E.idside=F.idside AND E.idclient='$client' AND F.idlang='$lang' ORDER BY D.idcatside";
	$db->query($sql);
	while ($db->next_record()){
		$seiten[$db->f('idcat')][$db->f('idcatside')]['name'] = $db->f('title');
		$seiten[$db->f('idcat')][$db->f('idcatside')]['idcatside'] = $db->f('idcatside');
		$seiten[$db->f('idcat')][$db->f('idcatside')]['is_start'] = $db->f('is_start');
		if($db->f('is_start') == 1){ $seiten['start'][$db->f('idcat')] = $db->f('idcatside'); }
		if(!is_array($seiten['sortarray'][$db->f('idcat')])){$seiten['sortarray'][$db->f('idcat')] = array(); }
		array_push($seiten['sortarray'][$db->f('idcat')],$db->f('sortindex'));
	}
	$baum = array();
	foreach($catlist as $a){
		$temp['id'] = $seiten['start'][$a];
		$temp['name'] = $ordner[$a];
		$temp['level'] = $catlist_level[$a];
		array_push($baum, $temp);
		if(is_array($seiten[$a])){
			array_multisort($seiten['sortarray'][$a] , SORT_ASC , $seiten[$a] , SORT_ASC);
			foreach(array_keys($seiten[$a]) as $b){
				if($seiten['start'][$a] != $b){
					$temp['id'] = $seiten[$a][$b]['idcatside'];
					$temp['name'] = $seiten[$a][$b]['name'];
					$temp['level'] = $catlist_level[$a] + 1;
					array_push($baum, $temp);
				}
			}
		}
	}
	unset($temp); unset($seiten); unset($ordner); unset($catlist); unset($catlist_level); unset($tlo_tree);
	$myout = "\n";
	foreach($baum as $a) $myout .= (str_repeat('  ',$a['level'])).$a['name'].";;http://dedilink/idcatside=".$a['id']."\n";
	$sql = "SELECT A.*, B.filetype, C.dirname FROM $dedi_db[upl] AS A left join $dedi_db[filetype] B on A.idfiletype=B.idfiletype left join $dedi_db[directory] C on A.iddirectory=C.iddirectory WHERE A.idclient='$client' ORDER BY C.dirname, A.filename";
	$db->query($sql);
	while ($db->next_record()) $myout .= $db->f('dirname').$db->f('filename').";;http://dedilink/fileid=".$db->f('idupl')."\n";
	print preg_replace("/\ /", "%20", $myout);
	unset($myout);
}

if ($GetImages == 'true') {
	$sql = "SELECT A.*, B.filetype, C.dirname FROM $dedi_db[upl] AS A left join $dedi_db[filetype] B on A.idfiletype=B.idfiletype left join $dedi_db[directory] C on A.iddirectory=C.iddirectory WHERE A.idclient='$client' AND (B.filetype=";
	$filetype = explode (',', 'jpg,gif,jpeg,png');
	$typ_j = count($filetype);
	$sql.= "'".$filetype['0']."'";
	for ($typ_i=1; $typ_i<=$typ_j; $typ_i++) $sql .= "OR B.filetype='".trim($filetype[$typ_i])."' ";
	unset($typ_j);
	unset($typ_i);
	$sql .= ") ORDER BY C.dirname, A.filename";
	$db->query($sql);
	$myout = $cfg_client['upl_htmlpath']."\n";
	while ($db->next_record()) $myout .= $db->f('dirname').$db->f('filename')."\n";
	print preg_replace("/\ /", "%20", $myout);
	unset($myout);
}
?>