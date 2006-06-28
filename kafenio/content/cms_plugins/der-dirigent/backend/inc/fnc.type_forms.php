<?PHP
/******************************************************************************
 @Author:     Björn Brockmann, Paul Eppner (paul@twomoons.de)
			      Sven Bräutigam, Jürgen Brändle
 @Homepage    http://www.der-dirigent.de
******************************************************************************/
include_once($dedi_path.'inc/fnc.type_common.php');

/**
 * Returns a HTML-hiddenfield
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 * @Return String HTML hiddenfield
 * @Access private
 */
function _type_get_element_hidden($formname, $content) {
	return '<input type="hidden" name="'. $formname .'" value="'. $content .'">';
}



/**
 * Returns a HTML-textfield
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 *        width
 *        maxlength
 * @Return String HTML textfield
 * @Access private
 */
function _type_get_element_text($formname, $content, $width, $maxlength) {
	$width = ( empty($width) ) ? '640px': $width;
	$maxlength = ( empty($maxlength) ) ? '': ' maxlength ="'. $maxlength .'" ';

	$out = '<input type="text" name="'. $formname .'" value="'. $content . '" size="90" style="width:' . $width . '" '. $maxlength .'>'."\n";
	return $out;
}



/**
 * Returns complete formatted HTML-textfield
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 *        type_config -> array with values from the DEDI:tag
 *        dedi_side['view'] -> the current view of the user, 'edit' or 'preview'
 *                 ['edit'] -> 'true' if user set the mod in templateconfig as active
 *                             if user set inactiv var is not set
 * @Return String HTML textfield
 * @Access public
 */
function type_form_text($formname, $content, $type_config, $dedi_side) {
	if (! _type_check_editable($dedi_side['edit'], $type_config['editable'], $dedi_side['view'])) return _type_get_element_hidden($formname, $content);
	else {
		$width = $type_config['width'];
		$maxlength = $type_config['maxlength'];
		$out = '<td class="content">'."\n"._type_get_element_text($formname, $content, $width, $maxlength)."\n</td>\n";
	}
	return $out;
}


/**
 * Returns complete formatted HTML-textareafield
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 *        type_config -> array with values from the DEDI:tag
 *        dedi_side['view'] -> the current view of the user, 'edit' or 'preview'
 *                 ['edit'] -> 'true' if user set the mod in templateconfig as active
 *                             if user set inactiv var is not set
 * @Return String HTML textareafield
 * @Access public
 */
function type_form_textarea($formname, $content, $type_config, $dedi_side) {
	if (!_type_check_editable($dedi_side['edit'], $type_config['editable'], $dedi_side['view'])) return _type_get_element_hidden($formname, $content);
	$width = $type_config['width'];
	$width = ( empty($width) ) ? '640px': $width;
	$height = $type_config['width'];
	$height = ( empty($height) ) ? '200px': $height;

	return '<td class="content">
	          <textarea name="'. $formname . '" rows="14" cols="52" style="width:'.$width.';height:'.$height.'">'. $content .'</textarea>
	        </td>'. "\n";
}


/**
 * Returns complete WYSIWYG-field for IE or JAVA
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 *        type_config -> array with values from the DEDI:tag
 *        dedi_side['view'] -> the current view of the user, 'edit' or 'preview'
 *                 ['edit'] -> 'true' if user set the mod in templateconfig as active
 *                             if user set inactiv var is not set
 * @Return String HTML textareafield
 * @Access public
 */
function type_form_wysiwyg($formname, $content, $type_config) {
	global $db, $dedi_db, $dedi_lang, $client, $cfg_dedi, $cfg_client, $tmp_sniffer;

	// Wenn kein ie > 5.5 oder applet ist immer sichtbar
	if (($tmp_sniffer['18'] != 'true' && $cfg_client['wysiwyg_applet'] == '1') || $cfg_client['wysiwyg_applet'] == '2') $out = _type_form_wysiwyg_java($formname, $content, $type_config);
	else $out = _type_form_wysiwyg_ie($formname, $content, $type_config);
	return $out;
}

function type_form_wysiwyg2($formname, $content, $type_config) {
	global $db, $dedi_db, $dedi_lang, $client, $cfg_dedi, $cfg_client, $tmp_sniffer;

	// Wenn kein ie > 5.5 oder applet ist immer sichtbar
	if (($tmp_sniffer['18'] != 'true' && $cfg_client['wysiwyg_applet'] == '1') || $cfg_client['wysiwyg_applet'] == '2') $out = _type_form_wysiwyg_java($formname, $content, $type_config);
	else $out = _type_form_wysiwyg_ie2($formname, $content, $type_config);
	return $out;
}



function _type_form_wysiwyg_ie($formname, $content, $type_config) {
	global $dedi_lang, $client, $cfg_dedi, $cfg_client, $sess;

	$out = '<script language="Javascript1.2" src="'.$sess->url($cfg_dedi['dedi_html_path'].'external/wysiwyg/wysiwyg.php').'"></script>'."\n";
	$out .= "    <td class=\"content\"><textarea name=\"$formname\" rows=\"20\" cols=\"52\" style=\"width:640\">$content</textarea></td>\n";
	//<a href="javascript:editor_insertHTML('box2','<font style=\'background-color: yellow\'>','</font>');">Highlight selected text</a> -
	$out .= '<script language="javascript1.2">'."\n";
	$out .= "if(document.all) fasteditor_generate('".$formname."');\n";
	$out .= '</script>'."\n";

	return $out;
}


function _type_form_wysiwyg_ie2($formname, $content, $type_config) {
	global $dedi_lang, $client, $lang, $dedi, $cfg_dedi, $cfg_client, $sess;

	$out  = '<script language="Javascript1.2" src="'.$sess->url($cfg_dedi['dedi_html_path'].'external/wysiwyg/wysiwyg2.php').'"></script>'."\n";
	$out .= '<script>'."\n";

	// Konfigurationsarray schreiben
	$out .= $formname.'_conf = new Array();'."\n";

	// Toolbar
	if (!empty($type_config['features']) && $type_config['features'] != 'true') {
		$featurestring = str_replace(' ', '', strtolower($type_config['features']) );
		$features = explode(',', $featurestring );
		$out .= _type_form_wysiwyg_ie2_build_menu($formname.'_conf', $features, $type_config['selectablestyles']);
	}

	// Mögliche Schriftarten
	if (!empty($type_config['selectablestyles'])) {
		$style_array = _type_get_stylelist($type_config['selectablestyles']);
		$c = count($style_array);
		if ($c > 0) {
			for($i=0; $i<$c; $i++) $jsstyle .= '{ name: "'. $style_array[$i]['autodesc'] .'", className: "'. $style_array[$i]['name'] .'", classStyle: "'. $style_array[$i]['style'] .'" },' . "\n";
			$jsstyle .= '{ name: "Styleangabe entfernen", className: "remove", classStyle: "" }';
			$out .= $formname."_conf['fontstyles'] = [".$jsstyle."];";
		}
	}

	// Parameter für Bildimport
	$out .= ($type_config['imagefiletypes'] == 'true' || empty($type_config['imagefiletypes'])) ? $formname."_conf['imagefiletypes'] = 'jpg,gif,png,jpeg';\n" : $formname."_conf['imagefiletypes'] = '".$type_config['imagefiletypes']."';\n";
	$out .= ($type_config['imagefolders'] == 'true' || empty($type_config['imagefolders'])) ? $formname."_conf['imagefolders'] = 'true';\n" : $formname."_conf['imagefolders'] = '".$type_config['imagefolders']."';\n";
	$out .= ($type_config['imagesubfolders'] == 'true') ? $formname."_conf['imagesubfolders'] = 'true';\n" : $formname."_conf['imagesubfolders'] = 'false';\n";

	// Parameter für Dateilinks
	$out .= ($type_config['filefiletypes'] == 'true' || empty($type_config['filefiletypes'])) ? $formname."_conf['filefiletypes'] = 'true';\n" : $formname."_conf['filefiletypes'] = '".$type_config['filefiletypes']."';\n";
	$out .= ($type_config['filefolders'] == 'true' || empty($type_config['filefolders'])) ? $formname."_conf['filefolders'] = 'true';\n" : $formname."_conf['filefolders'] = '".$type_config['filefolders']."';\n";
	$out .= ($type_config['filesubfolders'] == 'true') ? $formname."_conf['filesubfolders'] = 'true';\n" : $formname."_conf['filesubfolders'] = 'false';\n";

	// Style für den Editor
	$stylesheet = _type_get_stylesheet();
	if ($stylesheet != '') $out .= $formname."_conf['stylesheet'] = '".str_replace("\'","'", addslashes($stylesheet))."';";
	$css = _type_get_style($type_config['styleclass'], $type_config['styleid'], $type_config['styledb']);
	if (!empty($css['type'])) $out .= $formname."_conf['style'] = '".$css['fullstyle']."';";
	$out .= '</script>'."\n";

	// Textfeld erstellen
	$out .= "    <td class=\"content\"><textarea name=\"$formname\" rows=\"20\" cols=\"52\" style=\"width:640\">$content</textarea></td>\n";

	// Editor generieren
	$out .= '<script language="javascript1.2">'."\n";
	$out .= "if(document.all) editor_generate('$formname');\n";
	$out .= '</script>'."\n";
	return $out;
}

function _type_form_wysiwyg_ie2_build_menu($editor_conf_name, $features, $selectablestyles) {
	// 1. Zeile
	if (in_array('clipboardtools', $features)) $sub[] .= "'selectall','cut','copy','paste','delete'";
	if (in_array('striptag', $features)) $sub[] .= "'remove'";
	if (in_array('undo', $features)) $sub[] .= "'undo','redo'";
	if (in_array('search', $features)) $sub[] .= "'find'";
	if (in_array('changecase', $features)) $sub[] .= "'changecase'";
	if (count($sub) > 0) $row1 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('table', $features)) $sub[] .= "'InsertTable','showborder','TableProperties', 'RowProperties','InsertRowBefore','InsertRowAfter','DeleteRow','SplitRow', 'InsertColumnBefore','InsertColumnAfter','DeleteColumn', 'CellProperties','InsertCellBefore','InsertCellAfter','DeleteCell','SplitCell','MergeCells'";
	if (count($sub) > 0) $row1 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (!empty($row1)) $row1 = $row1."['linebreak'],\n";

	// 2. Zeile
	if (in_array('bold', $features)) $sub[] .= "'bold'";
	if (in_array('italic', $features)) $sub[] .= "'italic'";
	if (in_array('underline', $features)) $sub[] .= "'underline'";
	if (in_array('strikethrough', $features)) $sub[] .= "'strikethrough'";
	if (in_array('subscript', $features)) $sub[] .= "'subscript'";
	if (in_array('superscript', $features)) $sub[] .= "'superscript'";
	if (in_array('align', $features) ) $sub[] .= "'justifyleft','justifycenter','justifyright','justifyfull','justifynone'";
	if (count($sub) > 0) $row2 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('fontcolor', $features)) $sub[] .= "'forecolor'";
	if (in_array('backgroundcolor', $features)) $sub[] .= "'backcolor'";
	if (in_array('list', $features)) $sub[] .= "'OrderedList','UnOrderedList'";
	if (in_array('indent', $features)) $sub[] .= "'Outdent','Indent'";
	if (count($sub) > 0) $row2 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('link', $features)) $sub[] .= "'insertlink'";
	if (in_array('file', $features)) $sub[] .= "'insertfile'";
	if (in_array('link', $features) || in_array('file', $features)) $sub[] .= "'unlink'";
	if (in_array('image', $features)) $sub[] .= "'InsertImage'";
	if (in_array('liveresize', $features)) $sub[] .= "'liveresize'";
	if (in_array('multipleselect', $features)) $sub[] .= "'multipleselect'";
	if (count($sub) > 0) $row2 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('hr', $features)) $sub[] .= "'line'";
	if (in_array('specialchars', $features)) $sub[] .= "'specchar'";
	if (in_array('date', $features)) $sub[] .= "'today'";
	if (in_array('marquee', $features)) $sub[] .= "'marquee'";
	if (count($sub) > 0) $row2 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (!empty($row2)) $row2 = $row2."['linebreak'],\n";

	// 3. Zeile
	if (in_array('reset', $features)) $sub[] .= "'refresh'";
	if (in_array('preview', $features)) $sub[] .= "'preview'";
	if (in_array('print', $features)) $sub[] .= "'print'";
	if (count($sub) > 0) $row3 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('styles', $features) && !empty($selectablestyles)) $sub[] .= "'fontstyle'";
	if (in_array('font', $features)) $sub[] .= "'fontname'";
	if (in_array('fontsize', $features)) $sub[] .= "'fontsize'";
	if (count($sub) > 0) $row3 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	if (in_array('changemode', $features)) $sub[] .= "'htmlmode'";
	if (in_array('popupeditor', $features)) $sub[] .= "'popupeditor'";
	if (count($sub) > 0) $row3 .= '['.implode (', ', $sub)."],\n";
	unset($sub);
	return $editor_conf_name."['toolbar'] = [".$row1.$row2.$row3."];";
}

function _type_form_wysiwyg_java($formname, $content, $type_config) {
	global $db, $dedi_db, $idlay, $dedi_lang, $client, $cfg_dedi, $cfg_client, $lang, $sess, $idcatside, $entry;

	// SET DEFAULTS FOR EDITOR APPLET...
	// get stylesheet-files for this page (code taken from project's inc/frontend.php)
	// have to use absolute urls, because stylesheets are not relative to codebase
	$sql = "SELECT C.filetype, D.dirname, B.filename FROM $dedi_db[lay_upl] AS A LEFT JOIN $dedi_db[upl] AS B USING(idupl) LEFT JOIN $dedi_db[filetype] C on B.idfiletype=C.idfiletype left join $dedi_db[directory] D ON B.iddirectory=D.iddirectory WHERE idlay='$idlay'";
	$db->query($sql);
	while ($db->next_record()) if ($db->f('filetype') == 'css') $stylefiles .= $cfg_client['htmlpath'].$db->f('dirname').$db->f('filename').",";

	// un-htmlify content and encode to base64 before passing content to the applet
	$content = strtr($content, array_flip(get_html_translation_table(HTML_ENTITIES)));
	$b64content = base64_encode($content);

	// create "content" field for background saving of content from the applet.
	$contentfield = preg_replace("/(.*)_(\d+)_(\d+)_(\d+)_(\d+)/i", "\$2.\$3.\$4-\$5", $formname);

	// set server-side script that delivers image and file urls
	$servleturl = $sess->url($cfg_dedi['dedi_html_path'].'external/wysiwyg/applet.php?client='.$client.'&lang='.$lang);

	// turn on help menu by default.
	if (empty($type_config['features']) || $type_config['features'] == 'true') {
		// set default buttons for toolbar 1
		$buttons1 = "CUT,COPY,PASTE,SEPARATOR,BOLD,ITALIC,UNDERLINE,SEPARATOR,LEFT,CENTER,RIGHT,justify,SEPARATOR,STYLESELECT,DETACHFRAME";

		// set default buttons for toolbar 2
		$buttons2 = "ULIST,OLIST,SEPARATOR,DEINDENT,INDENT,SEPARATOR,ANCHOR,SEPARATOR,IMAGE,SEPARATOR,CLEARFORMATS,SEPARATOR,VIEWSOURCE,SEPARATOR,STRIKE,SUPERSCRIPT,SUBSCRIPT,INSERTCHARACTER,SEPARATOR,FIND,COLOR,TABLE,SEPARATOR,SAVECONTENT";

		// set default enable toolbar 1
		$enable_tooblar1 = 'true';

		// set default enable toolbar 2
		$enable_tooblar2 = 'true';

		// set view to start with
		$sourceview = 'false';
	} else {
		// add support for dedi-tags:
		$features = str_replace(' ', '', strtolower($type_config['features']) );
		$features = explode(',', $features);
		$components[] = '';
		if (in_array('font', $features)) $components[] = 'STYLES';
		if (in_array('fontsize', $features)) $menu_font = 'true';
		if (in_array('changemode', $features)) {
			$sourceview = 'true';
			$components[] = 'SOURCE';
			$components[] = 'SEPARATOR';
		} else {
			$sourceview = 'false';
		}
		if (in_array('clipboardtools', $features) ) {
			$menu_edit = 'true';
			$components[] = 'CUT';
			$components[] = 'COPY';
			$components[] = 'PASTE';
			$components[] = 'SEPARATOR';
		}
		if (in_array('striptag', $features)) {
			$menu_format = 'true';
			$components[] = 'CLEAR';
			$components[] = 'SEPARATOR';
		}
		if (in_array('undo', $features)) $menu_edit = 'true';
		if (in_array('search', $features)) {
			$menu_search = 'true';
			$components[] = 'FIND';
			$components[] = 'SEPARATOR';
		} else $menu_search = 'false';
		if (in_array('table', $features)) {
			$menu_table = 'true';
			$components[] = 'TABLE';
			$components[] = 'SEPARATOR';
		} else $menu_table = 'false';
		$itemset = 'false';
		if (in_array('bold', $features)) {
                 	$itemset = 'true';
                         $components[] = 'BOLD';
                 }
		if (in_array('italic', $features)) {
                 	$itemset = 'true';
                         $components[] = 'ITALIC';
                 }
		if (in_array('underline', $features)) {
                 	$itemset = 'true';
                         $components[] = 'UNDERLINE';
                 }
		if (in_array('strikethrough', $features)) {
                 	$itemset = 'true';
                         $components[] = 'STRIKE';
                 }
		if (in_array('subscript', $features)) {
                 	$itemset = 'true';
                         $components[] = 'SUB';
                 }
		if (in_array('superscript', $features)) {
                 	$itemset = 'true';
                         $components[] = 'SUPER';
                 }
		if ($itemset == 'true') $components[] = 'SEPARATOR';
		if (in_array('align', $features)) {
			$menu_format = 'true';
			$components[] = 'ALIGNLEFT';
			$components[] = 'ALIGNCENTER';
			$components[] = 'ALIGNRIGHT';
			$components[] = 'ALIGNJUSTIFIED';
			$components[] = 'SEPARATOR';
		}
		if (in_array('fontcolor', $features)) $components[] = 'COLOR';
		if (in_array('list', $features)) {
			$menu_format = 'true';
			$components[] = 'ULIST';
			$components[] = 'OLIST';
			$components[] = 'SEPARATOR';
		}
		if( in_array('indent', $features) ) {
			$components[] = 'INDENTLEFT';
			$components[] = 'INDENTRIGHT';
			$components[] = 'SEPARATOR';
		}
		if( in_array('link', $features) ) {
			$menu_insert = 'true';
			$components[] = 'ANCHOR';
		}
		if( in_array('file', $features) ) {
			$menu_insert = 'true';
			$components[] = 'ANCHOR';
		}
		if (in_array('image', $features)) $components[] = 'IMAGE';
		if (in_array('hr', $features)) $menu_insert = 'true';
		if (in_array('specialchars', $features)) $components[] = 'CHARACTER';

		// populate toolbars...
		$mycount = count($components);
		if ($mycount >= 14) {
			for ($myi = 0; $myi < 14; $myi++) {
				if ($myi == 0) $buttons1 .= $components[$myi];
				else if($myi > 0 && $myi < 14) $buttons1 .= ','.$components[$myi];
				else if ($myi == 14) $buttons2 .= $components[$myi];
				else if ($myi > 14) $buttons2 .= ','.$components[$myi];
			}
			$enable_tooblar1 = 'true';
			$enable_tooblar2 = 'true';
			$menu_edit = 'false';
			$menu_font = 'false';
			$menu_format = 'false';
			$menu_insert = 'false';
			$menu_table = 'false';
			$menu_forms = 'false';
			$menu_search = 'false';
			$menu_tools = 'false';
		}
	}

	// SET OUTPUT FOR EDITOR APPLET...
	$out  = '    <input type="hidden" name="'.$formname.'">';
	$out .= '<td>';
	$out .= '<APPLET CODEBASE="'.$cfg_client['htmlpath'].'" CODE="de.xeinfach.kafenio.KafenioApplet.class" ARCHIVE="cms/wysiwyg/kafenio.jar,cms/wysiwyg/gnu-regexp-1.1.4.jar,cms/wysiwyg/kafenio-config.jar,cms/wysiwyg/kafenio-icons.jar" NAME="WYSIWYG_'.$formname.'" ID="WYSIWYG_'.$formname.'" WIDTH="644" HEIGHT="400" MAYSCRIPT>';
	$out .= '<PARAM NAME="codebase" VALUE="'.$cfg_client['htmlpath'].'">';
	$out .= '<PARAM NAME="code" VALUE="de.xeinfach.kafenio.KafenioApplet.class">';
	$out .= '<PARAM NAME="archive" VALUE="cms/wysiwyg/kafenio.jar,cms/wysiwyg/gnu-regexp-1.1.4.jar,cms/wysiwyg/kafenio-config.jar,cms/wysiwyg/kafenio-icons.jar">';
	$out .= '<PARAM NAME="name" VALUE="WYSIWYG_'.$formname.'">';
	$out .= '<PARAM NAME="type" VALUE="application/x-java-applet">';
	$out .= '<PARAM NAME="scriptable" VALUE="true">';
	$out .= '<PARAM NAME="BASE64" VALUE="true">';
	$out .= '<PARAM NAME="STYLESHEET" VALUE="'.$stylefiles.'">';
	$out .= '<PARAM NAME="LANGCODE" VALUE="de">';
	$out .= '<PARAM NAME="LANGCOUNTRY" VALUE="DE">';
	$out .= '<PARAM NAME="TOOLBAR" VALUE="'.$enable_tooblar1.'">';
	$out .= '<PARAM NAME="TOOLBAR2" VALUE="'.$enable_tooblar2.'">';
	$out .= '<PARAM NAME="MENUBAR" VALUE="true">';
	$out .= '<PARAM NAME="MENUITEMS" VALUE="EDIT,VIEW,FONT,FORMAT,INSERT,TABLE,FORMS,SEARCH,TOOLS,HELP">';
	$out .= '<PARAM NAME="SOURCEVIEW" VALUE="'.$sourceview.'">';
	$out .= '<PARAM NAME="MENUICONS" VALUE="true">';
	$out .= '<PARAM NAME="BUTTONS" VALUE="'.$buttons1.'">';
	$out .= '<PARAM NAME="BUTTONS2" VALUE="'.$buttons2.'">';
	$out .= '<PARAM NAME="SERVLETURL" VALUE="'.$servleturl.'">';
	$out .= '<PARAM NAME="SERVLETMODE" VALUE="cgi">';
	$out .= '<PARAM NAME="SYSTEMID" VALUE="">';

	// z.b alle hidden-fields und die session-id beinhalten.
	$out .= '<PARAM NAME="POSTCONTENTURL" VALUE="';
	$out .= $cfg_client['htmlpath'].$sess->url($cfg_client['contentfile']."?view=edit&lang=$lang&action=save&entry=$entry&idcatside=$idcatside&content=$contentfield");
	$out .= '">';

	// contentparameter in dem der content übergeben werden soll.
	$out .= '<PARAM NAME="CONTENTPARAMETER" VALUE="'.$formname.'">';

	// outputmode kann normal oder base64 sein.
	$out .= '<PARAM NAME="OUTPUTMODE" VALUE="normal">';
	$out .= "<PARAM NAME=\"DOCUMENT\" VALUE=\"".$b64content."\">";
	$out .= "</APPLET>";
	$out .= "</td>";
	return $out;
}

function type_form_img($formname, $content, $type_config, $dedi_side) {
	global $db, $dedi_db, $dedi_lang, $client;

	//Standardfiletypes laden, wenn keine anderen angegeben, defaults laden
	$ft = strtolower(trim($type_config['filetypes']));
	$filetypes = ( empty($ft) || $ft == 'true' ) ? 'jpg,jpeg,gif,png': $ft;

	//get array of selectable images
	$image_array = _type_get_files($filetypes , $type_config['folders'], $type_config['subfolders']);
	$icount = count( $image_array );
	$optionfields= '<option value="">'.$dedi_lang['form_nothing'].'</option>' . "\n";
	for($temp_i = 0; $temp_i<$icount; $temp_i++) {
		$selected = ( $image_array[$temp_i]['id'] == $content ) ? ' selected ' : '';
		$optionfields .= '<option value="'. $image_array[$temp_i]['id'] .'"'. $selected . '>'. $image_array[$temp_i]['name'] .'</option>' ."\n";
	}
	$out = '<td class="content" width="640" nowrap><select name="'. $formname .'" size="1">'."\n";
	$out .= $optionfields;
	$out .= '</select></td>'. "\n";
	return $out;
}

function type_form_imgdescr($formname, $content, $type_config, $dedi_side) {
	global $db, $dedi_db, $dedi_lang, $client;

	return "    <td class=\"content\"><textarea name=\"$formname\" rows=\"2\" cols=\"52\" style=\"width:640\">$content</textarea></td>\n";
}


function type_form_link($formname, $content, $type_config, $dedi_side) {
	global $db, $dedi_db, $dedi_lang, $client, $lang, $catlist;

	$sql = "SELECT A.name, B.idcat, B.parent, B.sortindex FROM $dedi_db[cat_lang] AS A LEFT JOIN $dedi_db[cat] AS B USING(idcat) WHERE B.idclient='$client' ORDER BY B.parent, B.sortindex";
	$db->query($sql);
	while ($db->next_record()) {
		$ordner[$db->f('idcat')] = $db->f('name');
		$tlo_tree[$db->f('parent')][$db->f('sortindex')] = $db->f('idcat');
	}

	// tree_level_order_light();
	$sql = "SELECT D.idcatside, D.idcat, D.sortindex, D.is_start, F.title FROM $dedi_db[cat_side] AS D LEFT JOIN $dedi_db[side] AS E USING(idside) LEFT JOIN $dedi_db[side_lang] AS F USING(idside) WHERE E.idclient='$client' AND F.idlang='$lang' ORDER BY D.idcatside";
	$db->query($sql);
	while ($db->next_record()) {
		$seiten[$db->f('idcat')][$db->f('idcatside')]['name'] = $db->f('title');
		$seiten[$db->f('idcat')][$db->f('idcatside')]['idcatside'] = $db->f('idcatside');
		$seiten[$db->f('idcat')][$db->f('idcatside')]['is_start'] = $db->f('is_start');
		if ($db->f('is_start') == 1){ $seiten['start'][$db->f('idcat')] = $db->f('idcatside'); }
		if (!is_array($seiten['sortarray'][$db->f('idcat')])){$seiten['sortarray'][$db->f('idcat')] = array(); }
		array_push($seiten['sortarray'][$db->f('idcat')],$db->f('sortindex'));
	}
	$baum = array();
	foreach($catlist as $a){
		$temp['id'] = $seiten['start'][$a];
		$temp['name'] = $ordner[$a];
		$temp['level'] = $catlist_level[$a];
		array_push($baum, $temp);
		if(is_array($seiten[$a])) {
			array_multisort($seiten['sortarray'][$a] , SORT_ASC , $seiten[$a] , SORT_ASC);
			foreach(array_keys($seiten[$a]) as $b) {
				if($seiten['start'][$a] != $b) {
					$temp['id'] = $seiten[$a][$b]['idcatside'];
					$temp['name'] = $seiten[$a][$b]['name'];
					$temp['level'] = $catlist_level[$a] + 1;
					array_push($baum, $temp);
				}
			}
		}
	}
	$out = "    <input type=\"hidden\" name=\"$formname\" value=\"$content\">\n";
	if (is_numeric($content)) {
		$con_link_intern = $content;
		$con_link_extern = '';
	} else {
		$con_link_intern = '0';
		$con_link_extern = $content;
	}
	$out .= "      <td class=\"content\" width=\"640\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n";
	$out .= "        <tr>\n";
	$out .= "          <td class=\"content\">&nbsp;".$mod_lang['link_extern']."&nbsp;</td>\n";
	$out .= "          <td class=\"content\"><input type=\"text\" name=\"".$formname."extern\" value=\"".$con_link_extern."\" size=\"28\" style=\"width:219\" onchange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."intern.value='0';\"></td>\n";
	$out .= "          <td class=\"content\">&nbsp;".$mod_lang['link_intern']."&nbsp;</td>\n";
	$out .= "          <td class=\"content\"><select name=\"".$formname."intern\" size=\"1\" onChange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."extern.value='';\">\n";
	if ($con_link_intern == '0') $out .= "<option value=\"0\" selected>&nbsp;".$dedi_lang['form_nothing']."</option>\n";
	else $out .= "<option value=\"0\">&nbsp;".$dedi_lang['form_nothing']."</option>\n";
	foreach($baum as $a) {
		if ($con_link_intern == $a['id']) $out .= "<option value=\"".$a['id']."\" selected>".(str_repeat('&nbsp;&nbsp;',$a['level'])).$a['name']."</option>\n";
		else $out .= "<option value=\"".$a['id']."\">".(str_repeat('&nbsp;&nbsp;',$a['level'])).$a['name']."</option>\n";
	}
	$out .= "          </select></td>\n";
	$out .= "        </tr>\n";
	$out .= "      </table></td>\n";
	return $out;
}


function type_form_linkdescr($formname, $content, $type_config, $dedi_side) {
	return '<td class="content"><input type="text" name="'.$formname.'" style="width:400px" value="'.$content.'"></td>'."\n";
}


function type_form_linktarget($formname, $content, $type_config, $dedi_side) {
	global $mod_lang;

	$out = "    <input type=\"hidden\" name=\"$formname\" value=\"$content\">\n";
	$out .= "      <td class=\"content\" width=\"640\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n";
	$out .= "        <tr>\n";
	$out .= "          <td class=\"content\"><input type=\"text\" name=\"".$formname."frame\" value=\"$content\" size=\"11\" style=\"width:100\" onchange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."selectframe.value='_self';\"></td>\n";
	$out .= "          <td class=\"content\"><select name=\"".$formname."selectframe\" size=\"1\" onChange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."frame.value=this.value;\">\n";
	if ($content == '_self') $out .= "            <option value=\"_self\" selected>&nbsp;".$mod_lang['link_self']."</option>\n";
	else $out .= "            <option value=\"_self\">&nbsp;".$mod_lang['link_self']."</option>\n";
	if ($content == '_blank') $out .= "            <option value=\"_blank\" selected>&nbsp;".$mod_lang['link_blank']."</option>\n";
	else $out .= "            <option value=\"_blank\">&nbsp;".$mod_lang['link_blank']."</option>\n";
	if ($content == '_parent') $out .= "            <option value=\"_parent\" selected>&nbsp;".$mod_lang['link_parent']."</option>\n";
	else $out .= "            <option value=\"_parent\">&nbsp;".$mod_lang['link_parent']."</option>\n";
	if ($content == '_top') $out .= "            <option value=\"_top\" selected>&nbsp;".$mod_lang['link_top']."</option>\n";
	else $out .= "            <option value=\"_top\">&nbsp;".$mod_lang['link_top']."</option>\n";
	$out .= "          </select></td>\n";
	$out .= "        </tr>\n";
	$out .= "      </table></td>\n";
         return $out;
}

function type_form_sourcecode($formname, $content, $type_config, $dedi_side) {
	global $js_pad, $cfg_dedi, $gb_conf;

	include_once ($cfg_dedi['dedi_path'].'external/sourcepad/gb_source_pad.php');
	$js_pad = &new gb_source_pad('editcontent', $formname);

	$out = "    <td class=\"content\">\n";
	$js_pad -> set('handle_http_path', $cfg_dedi['dedi_html_path'].'external/sourcepad/');
	$js_pad -> set('language', 'german');
	$js_pad -> set('editorheight_css', '350px');
	$js_pad -> set('editorwidth_css', '640px');
	$js_pad -> set('content', $content);
	$out .= $js_pad -> make_pad();
	$out .= "    </td>\n";
	return $out;
}

/**
 * Returns complete formatted "select files from the filebrowser"- field
 *
 * @Args: formname -> name of the inputfield
 *        content -> value of textfield
 *        type_config -> array with values from the DEDI:tag
 *        dedi_side['view'] -> the current view of the user, 'edit' or 'preview'
 *                 ['edit'] -> 'true' if user set the mod in templateconfig as active
 *                             if user set inactiv var is not set
 * @Return String HTML textfield
 * @Access public
 */
function type_form_file($formname, $content, $type_config, $dedi_side) {
	global $db, $dedi_db, $dedi_lang, $client, $deditag_config;

	$filetypes = $type_config['filetypes'];
	$folders = $type_config['folders'];
	$subfolders = $type_config['subfolders'];

	//get files
	$file_list = _type_get_files($filetypes , $folders, $subfolders);

	$html_out = '
	<td class="content" width="640">
	 <table cellspacing="0" cellpadding="0" border="0">';
	//zusätzliche beschreibung nur wenn ein title im tag angegeben wurde, sonst würde hier zwei mal das gleiche stehen
	if(! empty($deditag_config[$con_type[$con_contype]['type']][$con_typenumber]['title'])){
	$html_out .= '
	  <tr>
	   <td class="content">&nbsp;'. $con_type[$con_contype]['descr'] .':</td>
	  </tr>';
	}

	$html_out .= '
	  <tr>
	   <td class="content">
		<select name="' . $formname . '" size="1">';

	//Keine Auswahl
	$html_out .= '<option value="">&nbsp;' . $dedi_lang['form_nothing'] .'</option>';

	$i = count($file_list);
	for($j=0; $j < $i; $j++)
	{
		if ($content == $file_list[$j]['id']){
			$html_out .= '<option value="' . $file_list[$j]['id'] . '" selected>'. $file_list[$j]['name'] .'</option>'."\n";
		}
		else{
			$html_out .= '<option value="' . $file_list[$j]['id'] . '">' . $file_list[$j]['name'] .'</option>'."\n";
		}
	}
	$html_out .= '</select>
	   </td>
	  </tr>
	 </table>
	</td>';

	//Hidetarget auslesen und variable ins filetarget rüberretten, da die Werte der dedi:tags nur im Haupttag sichtbar sind
	global $filetarget_is_hidden;
	$filetarget_is_hidden = $type_config['hidetarget'];
	return $html_out;
}

function type_form_filedescr($formname, $content, $type_config, $dedi_side) {
	return '<td class="content"><input type="text" name="'.$formname.'" style="width:400px" value="'.$content.'"></td>'."\n";
}

function type_form_filetarget($formname, $content, $type_config, $dedi_side) {
	global $mod_lang, $filetarget_is_hidden;

	if($filetarget_is_hidden != 'true'){
		$out = "    <input type=\"hidden\" name=\"$formname\" value=\"$content\">\n";
		$out .= "      <td class=\"content\" width=\"640\"><table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n";
		$out .= "        <tr>\n";
		$out .= "          <td class=\"content\"><input type=\"text\" name=\"".$formname."frame\" value=\"$content\" size=\"11\" style=\"width:100\" onchange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."selectframe.value='_self';\"></td>\n";
		$out .= "          <td class=\"content\"><select name=\"".$formname."selectframe\" size=\"1\" onChange=\"editcontent.".$formname.".value=this.value; editcontent.".$formname."frame.value=this.value;\">\n";
		if ($content == '_self') $out .= "            <option value=\"_self\" selected>&nbsp;".$mod_lang['link_self']."</option>\n";
		else $out .= "            <option value=\"_self\">&nbsp;".$mod_lang['link_self']."</option>\n";
		if ($content == '_blank') $out .= "            <option value=\"_blank\" selected>&nbsp;".$mod_lang['link_blank']."</option>\n";
		else $out .= "            <option value=\"_blank\">&nbsp;".$mod_lang['link_blank']."</option>\n";
		if ($content == '_parent') $out .= "            <option value=\"_parent\" selected>&nbsp;".$mod_lang['link_parent']."</option>\n";
		else $out .= "            <option value=\"_parent\">&nbsp;".$mod_lang['link_parent']."</option>\n";
		if ($content == '_top') $out .= "            <option value=\"_top\" selected>&nbsp;".$mod_lang['link_top']."</option>\n";
		else $out .= "            <option value=\"_top\">&nbsp;".$mod_lang['link_top']."</option>\n";
		$out .= "          </select></td>\n";
		$out .= "        </tr>\n";
		$out .= "      </table></td>\n";
	}
	unset($filetarget_is_hidden);
	return $out;
}


?>