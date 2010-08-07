/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.flotsam.limbo.ast;

import java.util.Set;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;

/**
 * A convenience {@link Node} wrapper around <code>String</code>s.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <E> 
 */
public class StringNode<E> extends AbstractNode<String, E> {

    private String value;

    public StringNode(String value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.ast.Node#eval(java.lang.Object)
     */
    public String eval(E context) {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.ast.Node#getType()
     */
    public Class<String> getType() {
        return String.class;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.ast.Node#simplify()
     */
    public Node<String, E> simplify() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Descriptive#document(nl.flotsam.limbo.Document)
     */
    public void document(Document target) {
        target.text("the String \"");
        target.text(value);
        target.text("\"");
    }

}
