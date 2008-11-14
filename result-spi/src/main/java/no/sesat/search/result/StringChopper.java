/* Copyright (2008) Schibsted Søk AS
 * This file is part of SESAT.
 *
 *   SESAT is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SESAT is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with SESAT.  If not, see <http://www.gnu.org/licenses/>.
 */

package no.sesat.search.result;

import java.util.Stack;

public class StringChopper {

    private enum State {
        none, tag, startTag, endTag, cdata, comment, declaration
    };

    /**
     * Truncate s to the given length at closest space or xml tag. Any xml tags will be closed/balanced.
     *
     * @param input The string that should be truncated.
     * @param length
     * @return The truncated string
     */
    public static String chop(final String input, final int length) {
        return chop(input, length, false);
    }

    /**
     * Truncate s to the given length or to closest space/tag depending on chop. Any xml tags will be closed/balanced.
     * @param input The string that should be truncated.
     * @param length max length of string (if choped the string will be '...' longer then max.)
     * @param chop If words should be choped, or if we chop inbetween spaces.
     * @return The truncated string
     */
    public static String chop(final String input, final int length, final boolean chop) {

        if (input == null)
            return null;

        Stack<Integer> stack = new Stack<Integer>();
        char[] s = input.toCharArray();
        StringBuilder res = new StringBuilder(s.length);
        State state = State.none;
        int count = 0;
        int i = 0;

        main: for (; i < s.length; i++) {
            char c = s[i];
            switch (state) {
            case none:
                if (c == '<') {
                    state = State.tag;
                } else {
                    count++;
                    if (count == length) {
                        res.append(c);
                        break main;
                    }
                }
                break;

            case tag:
                if (c == '/') {
                    state = State.endTag;
                } else if (c == '!') {
                    // ![CDATA[
                    if (s.length > (i + 7) && s[i + 1] == '[' && (s[i + 2] == 'C' || s[i + 2] == 'c')
                            && (s[i + 3] == 'D' || s[i + 3] == 'd') && (s[i + 4] == 'A' || s[i + 4] == 'a')
                            && (s[i + 5] == 'T' || s[i + 5] == 't') && (s[i + 6] == 'A' || s[i + 6] == 'a')
                            && s[i + 7] == '[') {
                        state = State.cdata;
                        res.append("![CDATA[");
                        i += 7;
                        continue;
                    }
                    // !--
                    else if (s.length > (i + 2) && s[i + 1] == '-' && s[i + 2] == '-') {
                        state = State.comment;
                        res.append("!--");
                        i += 2;
                        continue;
                    }
                } else if (c == '?') {
                    state = State.declaration;
                } else {
                    stack.push(i);
                    state = State.startTag;
                }
                break;

            case startTag:
                if (c == '/') {
                    if (s.length > (i + 1) && s[i + 1] == '>') {
                        state = State.none;
                        res.append("/>");
                        i += 1;
                        if(!stack.isEmpty())
                            stack.pop();
                        continue;
                    }
                } else if (c == '>') {
                    state = State.none;
                }
                break;

            case endTag:
                if (c == '>') {
                    state = State.none;
                    if(!stack.isEmpty())
                        stack.pop();
                }
                break;

            case cdata:

                if (c == ']') {// ]]>
                    if (s.length > (i + 2) && s[i + 1] == ']' && s[i + 2] == '>') {
                        state = State.none;
                        res.append("]]>");
                        i += 2;
                        continue;
                    }
                } else {
                    count++;
                    if (count == length) {
                        res.append(c);
                        break main;
                    }
                }
                break;

            case comment:
                if (c == '-') {
                    // -->
                    if (s.length > (i + 2) && s[i + 1] == '-' && s[i + 2] == '>') {
                        state = State.none;
                        res.append("-->");
                        i += 2;
                        continue;
                    }
                }
                break;

            case declaration:
                if (c == '?') {
                    if (s.length > (i + 1) && s[i + 1] == '>') {
                        state = State.none;
                        res.append("?>");
                        i += 1;
                        continue;
                    }
                }
                break;
            }
            res.append(c);
        }

        // append dots
        dot: if (i < s.length - 1) {
            if (chop) {
                res.append("...");
            } else {
                for (int k = i; k > 0; k--) {
                    if (s[k] == ' ' || s[k] == ((state == State.cdata) ? '[' : '>')) {
                        res.setLength(k + 1);
                        res.append("...");
                        break dot;
                    }
                }
                res.append("...");
            }
        }

        // close CDATA if we are in one
        if (state == State.cdata) {
            res.append("]]>");
        }

        // close all other open tags
        while (!stack.isEmpty()) {
            int j = stack.pop();
            char c = s[j];
            res.append("</");
            while (s.length > j && c != '>') {
                res.append(c);
                c = s[++j];
            }
            res.append('>');
        }

        return res.toString();
    }
}
