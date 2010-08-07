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
 * A node representing an integer literal.
 * 
 * @author Wilfred Springer
 * 
 */
public class IntegerNode<E> extends AbstractNode<Integer, E> {

    /**
     * The {@link Integer} value.
     */
    private Integer value;

    public IntegerNode(Integer value) {
        this.value = value;
    }

    public Class<Integer> getType() {
        return Integer.class;
    }

    public Node<Integer, E> simplify() {
        return this;
    }

    public static IntegerNode fromBin(String bin) {
        return new IntegerNode(Integer.parseInt(bin.substring(2), 2));
    }

    public static IntegerNode fromHex(String hex) {
        return new IntegerNode(Integer.parseInt(hex.substring(2), 16));
    }

    public Integer eval(E context) {
        return value;
    }

    public void gather(Set<Reference<E>> references) {
        // Nothing to add
    }

    public void document(Document target) {
        target.text(value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return false;
    }

}
