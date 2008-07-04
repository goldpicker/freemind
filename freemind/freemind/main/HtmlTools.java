/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: HtmlTools.java,v 1.1.2.19 2008-07-04 20:44:03 christianfoltin Exp $*/

package freemind.main;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/** */
public class HtmlTools {

    private static Logger logger;

    private static HtmlTools sInstance = new HtmlTools();

	private static final Pattern HTML_PATTERN = Pattern.compile("(?s)^\\s*<\\s*html.*?>.*");
    private static final Pattern FIND_TAGS_PATTERN = Pattern.compile("([^<]*)(<[^>]+>)");
    private static final Pattern SLASHED_TAGS_PATTERN = Pattern.compile("<(("+
    "br|area|base|basefont|"+
    "bgsound|button|col|colgroup|embed|hr"+
    "|img|input|isindex|keygen|link|meta"+
    "|object|plaintext|spacer|wbr"+
    ")(\\s[^>]*)?)/>");

    private static final Pattern TAGS_PATTERN = Pattern.compile("(?s)<[^><]*>");
    /**
     * 
     */
    private HtmlTools() {
        super();
        logger = Resources.getInstance().getLogger(HtmlTools.class.getName());
    }

    public static HtmlTools getInstance() {
        return sInstance;
    }

    public String toXhtml(String htmlText) {
        if(! isHtmlNode(htmlText)){
            return null;
        }
        logger.fine("Enter toXhtml with " + htmlText);
        StringReader reader = new StringReader(htmlText);
        StringWriter writer = new StringWriter();
        try {
            XHTMLWriter.html2xhtml(reader, writer);
            String resultXml = writer.toString();
            // for safety:
            if(!isWellformedXml(resultXml)) {
                return toXMLEscapedText(htmlText);
            }
            logger.fine("Leave toXhtml with " + resultXml);
            return resultXml;
        } catch (IOException e) {
            freemind.main.Resources.getInstance().logException(e);
        } catch (BadLocationException e) {
            freemind.main.Resources.getInstance().logException(e);
        }
        // fallback:
        htmlText = htmlText.replaceAll("<", "&gt;");
        htmlText = htmlText.replaceAll(">", "&lt;");
        logger.fine("Leave toXhtml with fallback " + htmlText);
        return htmlText;
    }

    public String toHtml(String xhtmlText) {
        // Remove '/' from <.../> of elements that do not have '/' there in HTML
        return SLASHED_TAGS_PATTERN.matcher(xhtmlText).replaceAll("<$1>");
    }

    public static class IndexPair {
        public int originalStart;
        public int originalEnd;
        public int replacedStart;
        public int replacedEnd;
        public boolean mIsTag;
        public boolean mIsAlreadyAppended = false;
        /**
         * @param pIsTag TODO
         */
        public IndexPair(int pOriginalStart, int pOriginalEnd, int pReplacedStart, int pReplacedEnd, boolean pIsTag)
        {
            super();
            
            originalStart = pOriginalStart;
            originalEnd = pOriginalEnd;
            replacedStart = pReplacedStart;
            replacedEnd = pReplacedEnd;
            mIsTag = pIsTag;
        }
        /**
         * generated by CodeSugar http://sourceforge.net/projects/codesugar */
        
        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("[IndexPair:");
            buffer.append(" originalStart: ");
            buffer.append(originalStart);
            buffer.append(" originalEnd: ");
            buffer.append(originalEnd);
            buffer.append(" replacedStart: ");
            buffer.append(replacedStart);
            buffer.append(" replacedEnd: ");
            buffer.append(replacedEnd);
            buffer.append(" is a tag: ");
            buffer.append(mIsTag);
            buffer.append("]");
            return buffer.toString();
        }
    }
    
    /** Replaces text in node content without replacing tags.
     * fc, 19.12.06: This method is very difficult. If you have a simplier
     * method, please supply it. But look that it complies with FindTextTests!!!
     */
    public String getReplaceResult(Pattern pattern, String replacement, String text) {
        ArrayList splittedStringList = new ArrayList();
        String stringWithoutTags = null;
        // remove tags and denote their positions:
        {
	        	StringBuffer sb = new StringBuffer();
            Matcher matcher = FIND_TAGS_PATTERN.matcher(text);
            int lastMatchEnd = 0;
            while (matcher.find())
            {
	            	String textWithoutTag = matcher.group(1);
	            	// Append text without tags:
	            	int replStart = sb.length();
                matcher.appendReplacement(sb, "$1");
                IndexPair indexPair;
				if (textWithoutTag.length()>0) {
					indexPair = new IndexPair(lastMatchEnd, matcher.end(1),
							replStart, sb.length(), false);
					lastMatchEnd = matcher.end(1);
//					System.out.println(sb.toString()
//							+ ", "
//							+ input.substring(indexPair.originalStart,
//									indexPair.originalEnd) + ", " + indexPair);
					splittedStringList.add(indexPair);
				}                
//				String tag = matcher.group(2);
				replStart = sb.length();
                indexPair = new IndexPair(lastMatchEnd, matcher.end(2),
                		replStart, sb.length(), true);
                lastMatchEnd = matcher.end(2);
//                System.out.println(sb.toString() + ", " + input.substring(indexPair.originalStart, indexPair.originalEnd)+ ", " + indexPair);
                splittedStringList.add(indexPair);
            }
            int replStart = sb.length();
            matcher.appendTail(sb);
            // append tail only if there is a tail
			if (sb.length() != replStart) {
				IndexPair indexPair = new IndexPair(lastMatchEnd, text
						.length(), replStart, sb.length(), false);
//				System.out.println(sb.toString() + ", " + indexPair);
				splittedStringList.add(indexPair);
			}            
//			System.out.println(sb.toString());
			stringWithoutTags = sb.toString();
        }
    
//        // give it out:
//        for (Iterator iter = splittedStringList.iterator(); iter.hasNext();) {
//			IndexPair pair = (IndexPair) iter.next();
//			System.out.println(text.substring(pair.originalStart, pair.originalEnd) + ", " + pair);
//		}
        
        Matcher matcher = pattern.matcher(stringWithoutTags);
        StringBuffer sbResult = new StringBuffer();
        if(matcher.find()) {
            /* now, take all from 0 to m.start() from original.
             * append the replaced text, append all removed tags from the original
             * that stays in between and append the rest.
             */
            int mStart = matcher.start();
            int mEnd = matcher.end();
            int state = 0;
            for (Iterator iter = splittedStringList.iterator(); iter.hasNext();) {
				IndexPair pair = (IndexPair) iter.next();
				switch(state){
				case 0:
					// before replacement start:
					if(!pair.mIsTag && pair.replacedStart <= mStart && pair.replacedEnd > mStart) {
						state = 1;
						// only up to start:
						sbResult.append(text.substring(pair.originalStart, pair.originalStart + mStart - pair.replacedStart));
						// the replacement:
						sbResult.append(replacement);
						// no break as the next case eventually applies inmediately.
					} else {
						sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
						break;
					}
				case 1:
					// during replacement:
					if(!pair.mIsTag && pair.replacedStart <= mEnd && pair.replacedEnd > mEnd) {
						state = 2;
						// only after the end:
						sbResult.append(text.substring(pair.originalStart + mEnd - pair.replacedStart, pair.originalEnd));
					} else {
						// only tags:
						if(pair.mIsTag)
							sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
					}
					break;
				case 2:
					// the rest:
					sbResult.append(text.substring(pair.originalStart, pair.originalEnd));
					break;
				}
			}
//            //take all from 0 to m.start() from original.
//            append(sbResult, input, splittedStringList, 0, mStart);
//            //append the replaced text
//            sbResult.append(sbReplacement.toString());
//            //append all removed tags from the original that stays in between
//            StringBuffer sbTemp = new StringBuffer();
//            append(sbTemp, input, splittedStringList, mStart, mEnd);
//            sbResult.append(sbTemp.toString().replaceAll(".*?(<[^>]+>)", "$1"));
//            //append the rest.
//            append(sbResult, input, splittedStringList, mEnd, stringWithoutTags.length());
        } else {
            // no change:
            sbResult.append(text);
        }
//        System.out.println("Result:'"+sbResult.toString()+"'");
        return sbResult.toString();
    }
    
    
    private void append(StringBuffer pResult, String pInput,
			ArrayList pListOfIndices, int pReducedStart, int pReducedEnd) {
		if (pReducedStart == pReducedEnd) {
			int minj = getMinimalOriginalPosition(pReducedStart, pListOfIndices);
			int maxj = getMaximalOriginalPosition(pReducedStart, pListOfIndices);
			pResult.append(pInput.substring(minj, maxj));
			return;
		}
		for (int i = pReducedStart; i < pReducedEnd; ++i) {
			int minj = getMinimalOriginalPosition(i, pListOfIndices);
			int maxj = getMaximalOriginalPosition(i, pListOfIndices);
			pResult.append(pInput.substring(minj, maxj));
		}
		// pResult.append(getChunk(pInput, pReducedEnd, pListOfIndices));
	}


    public int getMinimalOriginalPosition(int pI, ArrayList pListOfIndices)
    {
        for (Iterator iter = pListOfIndices.iterator(); iter.hasNext();)
        {
            IndexPair pair = (IndexPair) iter.next();
            if(pI >= pair.replacedStart && pI <= pair.replacedEnd) {
                return pair.originalStart + pI - pair.replacedStart;
            }
        }
        throw new IllegalArgumentException("Position "+pI+" not found.");
    }
    /**
	 * @return the maximal index i such that pI is mapped to i by removing all
	 *         tags from the original input.
	 */
    public int getMaximalOriginalPosition(int pI, ArrayList pListOfIndices)
    {
        for(int i = pListOfIndices.size() -1 ; i >= 0; --i) {
            IndexPair pair = (IndexPair) pListOfIndices.get(i);
            if(pI >= pair.replacedStart) {
				if (!pair.mIsTag) {
					return pair.originalStart + pI - pair.replacedStart;
				} else {
					return pair.originalEnd;
				}
            }
        }
        throw new IllegalArgumentException("Position "+pI+" not found.");
    }

    /**
     */
    public static boolean isHtmlNode(String text) {
    	for(int i = 0; i < text.length(); i++){
    		final char ch = text.charAt(i);
			if(ch == '<'){
    			break;
    		}
    		if(! Character.isWhitespace(ch) || i == text.length()){
    			return false;
    		}
    	}
    	return HTML_PATTERN.matcher(text.toLowerCase(Locale.ENGLISH)).matches();        
    }

    public static String unicodeToHTMLUnicodeEntity(String text) {
    	/*
		 * Heuristic reserve for expansion : factor 1.2
		 */
		StringBuffer result = new StringBuffer((int) (text.length() * 1.2)); 
		int intValue;
		char myChar;
		for (int i = 0; i < text.length(); ++i) {
			myChar = text.charAt(i);
			intValue = (int) text.charAt(i);
			if (intValue < 32 || intValue > 126) {
				result.append("&#x").append(Integer.toString(intValue, 16))
						.append(';');
			} else {
				result.append(myChar);
			}
		}
		return result.toString();
	}

    public static String unescapeHTMLUnicodeEntity(String text) {
		StringBuffer result = new StringBuffer(text.length());
		StringBuffer entity = new StringBuffer();
		boolean readingEntity = false;
		char myChar;
		for (int i = 0; i < text.length(); ++i) {
			myChar = text.charAt(i);
			if (readingEntity) {
				if (myChar == ';') {
					if (entity.charAt(0) == '#') {
						try {
							if (entity.charAt(1) == 'x') {
								result.append((char) Integer.parseInt(entity
										.substring(2), 16));
							} else {
								result.append((char) Integer.parseInt(entity
										.substring(1), 10));
							}
						} catch (NumberFormatException e) {
							result.append('&').append(entity).append(';');
						}
					} else {
						result.append('&').append(entity).append(';');
					}
					entity.setLength(0);
					readingEntity = false;
				} else {
					entity.append(myChar);
				}
			} else {
				if (myChar == '&') {
					readingEntity = true;
				} else {
					result.append(myChar);
				}
			}
		}
		if (entity.length() > 0) {
			result.append('&').append(entity);
		}
		return result.toString();
	}

    /** Removes all tags (<..>) from a string if it starts with "<html>..." to make it compareable.
     */
    public static String removeHtmlTagsFromString(String text) {
        if (HtmlTools.isHtmlNode(text)) {
            return removeAllTagsFromString(text); // (?s) enables that . matches newline.
        } else {
            return text;
        }
    }

    public static String removeAllTagsFromString(String text) {
        return TAGS_PATTERN.matcher(text).replaceAll("");
    }

    public static String htmlToPlain(String text) {
        return htmlToPlain(text, /*strictHTMLOnly=*/true); }

     public static String htmlToPlain(String text, boolean strictHTMLOnly) {
         // 0. remove all newlines
         // 1. replace newlines, paragraphs, and table rows
         // 2. remove XML tags
         // 3. replace HTML entities including &nbsp;
         // 4. unescape unicode entities
         // This is a very basic conversion, fixing the most annoying
         // inconvenience.  You can imagine much better conversion of
         // HTML to plain text. Most of HTML tags can be handled
         // sensibly, like web browsers do it.
         if (strictHTMLOnly && !isHtmlNode(text)) {
            return text; }
         //System.err.println("base:"+text);
         String intermediate = text.
            replaceAll("(?ims)[\n\t]","").        // Remove newlines
            replaceAll("(?ims) +"," ").           // Condense spaces
            replaceAll("(?ims)<br.*?>","\n").
            replaceAll("(?ims)<p.*?>","\n\n").    // Paragraph
            replaceAll("(?ims)<div.*?>","\n").    // Div - block
            replaceAll("(?ims)<tr.*?>","\n").
            replaceAll("(?ims)<dt.*?>","\n").     // Defined term
            replaceAll("(?ims)<dd.*?>","\n   ").  // Definition of defined term
            replaceAll("(?ims)<td.*?>"," ").
            replaceAll("(?ims)<[uo]l.*?>","\n").  // Beginning of a list
            replaceAll("(?ims)<li.*?>","\n   * ").
            replaceAll("(?ims) *</[^>]*>","").    // Remaining closing HTML tags
            replaceAll("(?ims)<[^/][^>]*> *",""). // Remaining opening HTML tags
            // FIXME Dimitry: is removing of all new lines at the begin a good idea? 
            replaceAll("^\n+", "").
            // fc: to remove start and end spaces.
            trim();

         intermediate = HtmlTools.unescapeHTMLUnicodeEntity(intermediate);

         // Entities, with the exception of &.

         intermediate = intermediate.
            replaceAll("(?ims)&lt;", "<").replaceAll("(?ims)&gt;", ">").
            replaceAll("(?ims)&quot;", "\"").
            replaceAll("(?ims)&nbsp;", " ");
         //System.err.println("intermediate:"+intermediate);
         return intermediate.replaceAll("(?ims)&amp;", "&"); }

    public static String plainToHTML(String text) {
       char myChar;
       String textTabsExpanded = text.replaceAll("\t","         "); // Use eight spaces as tab width.
       StringBuffer result = new StringBuffer(textTabsExpanded.length()); // Heuristic
       int lengthMinus1 = textTabsExpanded.length() - 1;
       result.append("<html><body><p>");
       for (int i = 0; i < textTabsExpanded.length(); ++i) {
          myChar = textTabsExpanded.charAt(i);
          switch (myChar) {
          case '&': result.append("&amp;"); break;
          case '<': result.append("&lt;"); break;
          case '>': result.append("&gt;"); break;
          case ' ':
             if ( i > 0 && i < lengthMinus1 &&
                  (int)textTabsExpanded.charAt(i-1) > 32 && (int)textTabsExpanded.charAt(i+1) > 32 ) {
                result.append(' '); }
             else {
                result.append("&nbsp;"); }
             break;
          case '\n': result.append("<br>"); break;
          default:  result.append(myChar); }}
       return result.toString(); }

    
    public static String toXMLUnescapedText(String text) {
        return text.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll(
                "&quot;", "\"").replaceAll("&amp;", "&");
    }

    public static String toXMLEscapedTextExpandingWhitespace(String text) {
        // Spaces and tabs are handled
        text = text.replaceAll("\t","         "); // Use eight spaces as tab width.
        int len = text.length();
        StringBuffer result = new StringBuffer(len);
        char myChar;
        for (int i = 0; i < len; ++i) {
            myChar = text.charAt(i);
            switch (myChar) {
            case '&': result.append("&amp;"); break;
            case '<': result.append("&lt;"); break;
            case '>': result.append("&gt;"); break;
            case ' ':
               if ( i > 0 && i < len-1 &&
                    (int)text.charAt(i-1) > 32 && (int)text.charAt(i+1) > 32 ) {
                  result.append(' '); }
               else {
                  result.append("&nbsp;"); }
               break;
            default:
                result.append(myChar);
            }
        }
        return result.toString();
    }

    public static String toXMLEscapedText(String text) {
        return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
    }

    /**
     * @return true, if well formed XML.
     */
    public boolean isWellformedXml(String xml) {
        try {
            // Create a builder factory
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);

            // Create the builder and parse the file
            factory.newSAXParser().parse(new InputSource(new StringReader(xml)), new DefaultHandler());
            return true;
        } catch (SAXParseException e) {
            logger.log(Level.SEVERE, "XmlParseError on line " + e.getLineNumber() + " of " + xml, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "XmlParseError", e);
        }
        return false;
    }
    

}
