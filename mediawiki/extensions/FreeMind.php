<?php
// Freemind mindmap WikiMedia extension
// (C) Dimitry Polivaev 2006
// Example:
$wgExtensionFunctions[] = "wfFreemindExtension";

function wfFreemindExtension()
{
    global $wgParser;
    // Defines the tag <mindmap> ... </mindmap>
    // The second parameter is the callback function for
    // processing the text between the tags
    $wgParser->setHook("mm", "renderMindmap");
}
// The callback function for converting the input text to HTML output
function renderMindmap($input)
{
    // Default parameter values:
    $mm_height = "450";
    $mm_type = "flash";
    $mm_target = "embedded";

    if (preg_match('/^\s*\[{2}\s*:\s*(\w.*)\]{2}\s*$/', $input, $matches)) {
        $mm_target = "link";
        $input = $matches[1];
    } else
    if (preg_match('/^\s*\[{2}\s*(\w.*)\]{2}\s*$/', $input, $matches)) {
        $mm_target = "embedded";
        $input = $matches[1];
    } else{
    	return MindmapHelp($input);
	}

    $mm_title = "";
    $mm_description = "";

    $paramVector = explode("|", $input);
    $url = $paramVector[0];
    $paramNumber = count($paramVector);
    for ($i = 1; $i < $paramNumber; $i++) {
        $param = trim($paramVector[$i]);
        if (preg_match('/(\w+)\s+(.*)/', $param, $pair)) {
            if ("height" === $pair[1]) {
                $mm_height = $pair[2];
            } else if ("title" === $pair[1]) {
                $mm_title = $pair[2];
            } else if ("parameters" === $pair[0]) {
                preg_match_all('/(\\w+?)\\s*=\\s*"(.+?)"/', $pair[1], $match, PREG_SET_ORDER);
                foreach ($match as $i) $params[$i[1]] = $i[2];
            } else {
                if ($mm_description != "")
                    $mm_description .= '|';
                $mm_description .= $param;
            }
        } else {
            if ("flash" === $param || "applet" === $param) {
                $mm_type = $param;
            } else {
                $mm_description .= $param;
            }
        }
    }

    if ($mm_description === "") {
        $mm_description = $url;
    }

    if ($mm_title === "") {
        $mm_title = $url;
    }

    $img = new Image($url);
    $url = $img->getViewURL(false);

    global $wgServer, $wgScriptPath, $wgTitle, $wgUrlProtocols, $wgUser;
    static $flashContentCounter = 0;
    if ($mm_type === "flash") {
        $params['initLoadFile'] = $url;
        if (isset($params['openUrl'])) unset($params['openUrl']);
        if (! isset($params['startCollapsedToLevel'])) $params['startCollapsedToLevel'] = "5";
        if (strcasecmp($mm_target, "embedded") == 0) {
            $flashContentCounter++;
            require_once("freemind/flashwindowFunction.php");
            $output = getMindMapFlashOutput($mm_title, $params, $flashContentCounter, $mm_height, "$wgScriptPath/extensions/freemind/");
        } else if (strcasecmp($mm_target, "link") == 0) {
            $Formcounter++;
            $ref = "$wgScriptPath/extensions/freemind/flashwindow.php?";
        } else {
            $output = MindmapHelp($url);
        }
    } else if ($mm_type === "applet") {
        $server = $_SERVER['SERVER_NAME'];
        $params['browsemode_initial_map'] = "http://$server$url";
        if (isset($params['type'])) unset($params['type']);
        if (isset($params['scriptable'])) unset($params['scriptable']);
        if (isset($params['modes'])) unset($params['modes']);
        if (isset($params['initial_mode'])) unset($params['initial_mode']);

        if (strcasecmp($mm_target, "embedded") == 0) {
            require_once("freemind/appletwindowFunction.php");
            $output = getMindMapAppletOutput($mm_title, $params, $mm_height, "$wgScriptPath/extensions/freemind/");
        } else if (strcasecmp($mm_target, "link") == 0) {
            $ref = "$wgScriptPath/extensions/freemind/appletwindow.php?";
        } else {
            $output = MindmapHelp($url);
        }
    } else {
        $output = MindmapHelp($url);
    }
    if (! isset($output) && $mm_target === "link") {
        $params['mm_title'] = rawurlencode($mm_title);
        foreach ($params as $key => $value) {
            $ref .= "$key=$value&";
        }
        $ref = substr($ref, 0, -1);
        $output .= "<a href=$ref>$mm_description</a>";
    }
    // print($output);
    if ($mm_target == "embedded")
        $output = "$output";
    return $output;
}

function MindmapHelp($link)
{
    return "<div style='border: solid red 1px'>
<p><b>Syntax error or wrong link</b>: <i>$link</i>.</p><br>
<p><b>Syntax: </b>
<blockquote><b><tt>&lt;mindmap&gt; &lt;param <i>width=\"x%\"</i> <i>height=\"x%\"</i> <i>type=\"[flash|java]\"</i> <i>target=\"[embedded|link]\"</i>/&gt;<i>Link_To_MM_File</i>&lt;/mindmap&gt;</tt></b></blockquote><br>
<b>Example</b>
<blockquote>
<ul>
<li>&lt;mindmap&gt; &lt;param width=\"100%\" height=\"500px\" type=\"flash\" target=\"embedded\" /&gt;[http://www.worldhello.net/doc/wiki.mm Johnson's Wiki tutorial]&lt;/mindmap&gt;
<li>&lt;mindmap&gt; &lt;param type=\"java target=\"link\" /&gt;New upload mm file: [[Media:Wiki.mm]]&lt;/mindmap&gt;
<li>&lt;mindmap&gt;{{SERVER}}/doc/Wiki.mm&lt;/mindmap&gt;
</ul></blockquote>
</div>\n";
}

?>
