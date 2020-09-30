package project.rest.client.mining;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ContentFiltersTest {

    @Test
    void testRowspan(){
        Assertions.assertEquals(ContentFilters.rowspan("<td></td>"),0);
        Assertions.assertEquals(ContentFilters.rowspan("<td rowspan=\"1\"></td>"),1);
        Assertions.assertEquals(ContentFilters.rowspan("<td rowspan=\"2\"></td>"),2);
        Assertions.assertEquals(ContentFilters.rowspan("<td rowspan=\"3\"></td>"),3);
    }

    @Test
    void testExtract(){
        Assertions.assertEquals(ContentFilters.extract("<h2><span class=\"mw-headline\" id=\"Definition\">Definition</span><span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span><a href=\"/w/index.php?title=Longest_flights&amp;action=edit&amp;section=1\" title=\"Edit section: Definition\">edit</a><span class=\"mw-editsection-bracket\">]</span></span></h2>"),"Definition");
    }

    @Test
    void testClean(){
        Assertions.assertEquals(ContentFilters.clean("#)Title"), "no_Title");
    }

}
