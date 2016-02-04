/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kalang.test;

import jast.ast.AstNode;
import kalang.compiler.JavaAstLoader;
import kalang.compiler.SourceParser;
import kalang.util.SourceParserFactory;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kason Yang <i@kasonyang.com>
 */
public class SourceParserTest {
    
    public SourceParserTest() {
    }
    
    @Test
    public void test(){
        SourceParser sp = SourceParserFactory.createSourceParser("Test", "class{"
                + "void main(){"
                + "}"
                + "}");
        sp.compile(new JavaAstLoader());
        ParseTree tree = sp.getParseTreeByTokenIndex(0);
        assertNotNull(tree);
        ParseTree treeMd = sp.getParseTreeByTokenIndex(2);
        ParseTree treeMdEnd = sp.getParseTreeByTokenIndex(7);
        assertEquals(treeMd, treeMdEnd.getParent().getParent());
        AstNode ast = sp.getAstNode(tree);
        assertNotNull(ast);
    }
    
}
