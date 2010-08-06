/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.codehaus.preon.sample.bytecode;

import static org.codehaus.preon.buffer.ByteOrder.BigEndian;

import org.codehaus.preon.annotation.BoundBuffer;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.annotation.Choices;
import org.codehaus.preon.annotation.Init;
import org.codehaus.preon.annotation.TypePrefix;
import org.codehaus.preon.annotation.Choices.Choice;


/**
 * An attempt to capture Java's class file format in Preon.
 *
 * @author Wilfred Springer
 */
// START SNIPPET: sample
public class ClassFile {

    @BoundBuffer(match = {
            (byte) 0xca,
            (byte) 0xfe,
            (byte) 0xba,
            (byte) 0xbe})
    private byte[] magic;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int minorVersion;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int majorVersion;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int constantPoolCount;

    @BoundList(size = "constantPoolCount-1", types = {
            ClassCpInfo.class,
            DoubleCpInfo.class,
            FieldRefCpInfo.class,
            FloatCpInfo.class,
            IntegerCpInfo.class,
            InterfaceMethodRefCpInfo.class,
            MethodRefCpInfo.class,
            NameAndTypeCpInfo.class,
            StringCpInfo.class,
            Utf8CpInfo.class})
    private CpInfo[] constantPool;

    // END SNIPPET: sample

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int accessFlags;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int thisClass;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int superClass;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int interfacesCount;

    @BoundList(size = "interfacesCount")
    private int[] interfaces;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int fieldCount;

    @BoundList(size = "fieldCount")
    private FieldInfo[] fields;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int methodCount;

    @BoundList(size = "methodCount")
    private MethodInfo[] methods;

    @BoundNumber(size = "16", byteOrder = BigEndian)
    private int attributeCount;

    @BoundList(size = "attributeCount", selectFrom = @Choices(prefixSize = 16, byteOrder = BigEndian, alternatives = {
            @Choice(condition = "constantPool[prefix-1].value=='SourceFile'", type = SourceFile.class),
            @Choice(condition = "constantPool[prefix-1].value=='Deprecated'", type = Deprecated.class)}))
    private AttributeInfo[] attributes;

    @TypePrefix(value = "7", size = 8)
    public class ClassCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameIndex;

        public String getName() {
            return constantPool[nameIndex].toString();
        }

        public String toString() {
            return "name: " + nameIndex;
        }

    }

    @TypePrefix(value = "1", size = 8)
    public class Utf8CpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int length;

        @BoundString(size = "length")
        private String value;

        public String getStringValue() {
            return value;
        }

        public String toString() {
            return "\"" + value + "\"";
        }

    }

    @TypePrefix(value = "9", size = 8)
    public class FieldRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameAndTypeIndex;

    }

    @TypePrefix(value = "10", size = 8)
    public class MethodRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameAndTypeIndex;

        public String toString() {
            return "class index: " + classIndex + "; name and type index: " + nameAndTypeIndex;
        }

    }

    @TypePrefix(value = "11", size = 8)
    public class InterfaceMethodRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameAndTypeIndex;

    }

    @TypePrefix(value = "8", size = 8)
    public class StringCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int stringIndex;

        public String toString() {
            return "string index: " + stringIndex;
        }

    }

    @TypePrefix(value = "3", size = 8)
    public class IntegerCpInfo extends CpInfo {

        @BoundNumber(byteOrder = BigEndian)
        private int value;

    }

    @TypePrefix(value = "4", size = 8)
    public class FloatCpInfo extends CpInfo {

        @BoundNumber(byteOrder = BigEndian)
        private float value;

    }

    @TypePrefix(value = "6", size = 8)
    public class DoubleCpInfo extends CpInfo {

        @BoundNumber(byteOrder = BigEndian)
        private double value;

    }

    @TypePrefix(value = "5", size = 8)
    public class LongCpInfo extends CpInfo {

        @BoundNumber(byteOrder = BigEndian)
        private long value;

    }

    @TypePrefix(value = "12", size = 8)
    public class NameAndTypeCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int descriptorIndex;

    }

    public class MethodInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int accessFlags;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int descriptorIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int attributesCount;

        @BoundList(size = "attributesCount", selectFrom = @Choices(prefixSize = 16, byteOrder = BigEndian, alternatives = {
                @Choice(condition = "outer.constantPool[prefix-1].value=='Code'", type = Code.class),
                @Choice(condition = "outer.constantPool[prefix-1].value=='Exceptions'", type = Exceptions.class),
                @Choice(condition = "outer.constantPool[prefix-1].value=='Synthetic'", type = Synthetic.class),
                @Choice(condition = "outer.constantPool[prefix-1].value=='Deprecated'", type = Deprecated.class)}))
        private Object[] attributes;

        public String getName() {
            return ((Utf8CpInfo) ClassFile.this.constantPool[nameIndex - 1]).getStringValue();
        }

        public String getDescriptor() {
            return ((Utf8CpInfo) ClassFile.this.constantPool[descriptorIndex - 1]).getStringValue();
        }

        public class Code extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int maxStack;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int maxLocals;

            @BoundNumber(size = "32", byteOrder = BigEndian)
            private long codeLength;

            @BoundList(size = "codeLength")
            private byte[] code;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int exceptionTableLength;

            @BoundNumber(size = "exceptionTableLength")
            private ExceptionTableEntry[] exceptionTable;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int attributesCount;

            @BoundList(size = "attributesCount", selectFrom = @Choices(prefixSize = 16, byteOrder = BigEndian, alternatives = {
                    @Choice(condition = "outer.outer.constantPool[prefix-1].value=='LineNumberTable'", type = LineNumberTable.class),
                    @Choice(condition = "outer.outer.constantPool[prefix-1].value=='LocalVariableTable'", type = LocalVariableTable.class)}))
            private AttributeInfo[] attributes;

            public class LineNumberTable extends AttributeInfo {

                @BoundNumber(size = "16", byteOrder = BigEndian)
                int lineNumberTableLength;

                @BoundList(size = "lineNumberTableLength")
                LineNumberTableEntry[] lineNumberTable;

                public class LineNumberTableEntry {

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int startPc;

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int lineNumber;

                }

            }

            public class LocalVariableTable extends AttributeInfo {

                @BoundNumber(size = "16", byteOrder = BigEndian)
                private int localVariableTableLength;

                @BoundList(size = "localVariableTableLength")
                private LocalVariableTableEntry[] localVariableTable;

                public class LocalVariableTableEntry {

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int startPc;

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int length;

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int nameIndex;

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int descriptorIndex;

                    @BoundNumber(size = "16", byteOrder = BigEndian)
                    private int index;

                }

            }

        }

        public class ExceptionTableEntry {

            @BoundNumber(size = "16", byteOrder = BigEndian)
            int startPc;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            int endPc;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            int handlerPc;

            @BoundNumber(size = "16", byteOrder = BigEndian)
            int catchType;

        }

        public class Exceptions extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int numberOfExceptions;

            @BoundList(size = "numberOfExceptions")
            private int[] exceptionIndexTable;

        }

    }

    public class SourceFile extends AttributeInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int sourceFileIndex;

        @Init
        public void init() {
            System.err.println(getName());
        }

        public String getName() {
            return ((Utf8CpInfo) constantPool[sourceFileIndex - 1]).getStringValue();
        }

    }

    public class FieldInfo {

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int accessFlags;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int descriptorIndex;

        @BoundNumber(size = "16", byteOrder = BigEndian)
        private int attributesCount;

        @BoundList(size = "attributesCount", selectFrom = @Choices(prefixSize = 16, byteOrder = BigEndian, alternatives = {
                @Choice(condition = "outer.constantPool[prefix-1].value=='ConstantValue'", type = ConstantValue.class),
                @Choice(condition = "outer.constantPool[prefix-1].value=='Synthetic'", type = Synthetic.class),
                @Choice(condition = "outer.constantPool[prefix-1].value=='Deprecated'", type = Deprecated.class)}))
        private AttributeInfo[] attributes;

        public String getDescriptor() {
            return ((Utf8CpInfo) constantPool[descriptorIndex]).getStringValue();
        }

        private class ConstantValue extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = BigEndian)
            private int constantValueIndex;

        }

    }

    private static class Synthetic extends AttributeInfo {

    }

    private static class Deprecated extends AttributeInfo {

    }

    public MethodInfo[] getMethods() {
        return methods;
    }

}
