/*
 * Copyright (C) 2008 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

package nl.flotsam.preon.sample.bytecode;

import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.annotation.BoundString;
import nl.flotsam.preon.annotation.Choices;
import nl.flotsam.preon.annotation.Init;
import nl.flotsam.preon.annotation.TypePrefix;
import nl.flotsam.preon.annotation.Choices.Choice;
import nl.flotsam.preon.buffer.ByteOrder;

/**
 * An attempt to capture Java's class file format in Preon.
 * 
 * @author Wilfred Springer
 * 
 */
public class ClassFile {

    @BoundNumber(size = "32", byteOrder = ByteOrder.BigEndian)
    private long magic;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int minorVersion;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int majorVersion;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int constantPoolCount;

    @BoundList(size = "constantPoolCount-1", types = { ClassCpInfo.class, DoubleCpInfo.class,
            FieldRefCpInfo.class, FloatCpInfo.class, IntegerCpInfo.class,
            InterfaceMethodRefCpInfo.class, MethodRefCpInfo.class, NameAndTypeCpInfo.class,
            StringCpInfo.class, Utf8CpInfo.class })
    private CpInfo[] constantPool;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int accessFlags;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int thisClass;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int superClass;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int interfacesCount;

    @BoundList(size = "interfacesCount")
    private int[] interfaces;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int fieldCount;

    @BoundList(size = "fieldCount")
    private FieldInfo[] fields;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int methodCount;

    @BoundList(size = "methodCount")
    private MethodInfo[] methods;

    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
    private int attributeCount;

    @BoundList(size = "attributeCount", type = AttributeInfo.class)
    private AttributeInfo[] attributes;

    @TypePrefix(value = "7", size = 8)
    public class ClassCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameIndex;

        public String getName() {
            return constantPool[nameIndex].toString();
        }

        @Init
        public void init() {
            System.out.println("Name index " + nameIndex);
        }

    }

    @TypePrefix(value = "1", size = 8)
    public class Utf8CpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int length;

        @BoundString(size = "length")
        private String value;

        public String getStringValue() {
            return value;
        }

        @Init
        public void init() {
            System.out.println("UTF 8 " + getStringValue());
        }

    }

    @TypePrefix(value = "9", size = 8)
    public class FieldRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameAndTypeIndex;

    }

    @TypePrefix(value = "10", size = 8)
    public class MethodRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameAndTypeIndex;

    }

    @TypePrefix(value = "11", size = 8)
    public class InterfaceMethodRefCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int classIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameAndTypeIndex;

    }

    @TypePrefix(value = "8", size = 8)
    public class StringCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int stringIndex;

    }

    @TypePrefix(value = "3", size = 8)
    public class IntegerCpInfo extends CpInfo {

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private int value;

    }

    @TypePrefix(value = "4", size = 8)
    public class FloatCpInfo extends CpInfo {

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private float value;

    }

    @TypePrefix(value = "6", size = 8)
    public class DoubleCpInfo extends CpInfo {

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private double value;

    }

    @TypePrefix(value = "5", size = 8)
    public class LongCpInfo extends CpInfo {

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private long value;

    }

    @TypePrefix(value = "12", size = 8)
    public class NameAndTypeCpInfo extends CpInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int descriptorIndex;

    }

    public class MethodInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int accessFlags;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int descriptorIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int attributesCount;

        @BoundList(size = "attributesCount", type = AttributeInfo.class)
        private AttributeInfo[] attributes;

        public String getName() {
            return ((Utf8CpInfo) ClassFile.this.constantPool[nameIndex]).getStringValue();
        }

        public String getDescriptor() {
            return ((Utf8CpInfo) ClassFile.this.constantPool[descriptorIndex]).getStringValue();
        }

        public class Code extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            private int maxStack;

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            private int maxLocals;

            @BoundNumber(size = "32", byteOrder = ByteOrder.BigEndian)
            private long codeLength;

            @BoundList(size = "codeLength")
            private byte[] code;

            @BoundNumber(size = "32", byteOrder = ByteOrder.BigEndian)
            private int exceptionTableLength;

            @BoundNumber(size = "exceptionTableLength")
            private ExceptionTableEntry[] exceptionTable;

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            private int attributesCount;

            @BoundList(size = "attributesCount", type = AttributeInfo.class)
            private AttributeInfo[] attributes;

            public class LineNumberTable extends AttributeInfo {

                @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                int lineNumberTableLength;

                @BoundList(size = "lineNumberTableLength")
                LineNumberTableEntry[] lineNumberTable;

                public class LineNumberTableEntry {

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int startPc;

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int lineNumber;

                }

            }

            public class LocalVariableTable extends AttributeInfo {

                @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                private int localVariableTableLength;

                @BoundList(size = "localVariableTableLength")
                private LocalVariableTableEntry[] localVariableTable;

                public class LocalVariableTableEntry {

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int startPc;

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int length;

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int nameIndex;

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int descriptorIndex;

                    @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
                    private int index;

                }

            }

        }

        public class ExceptionTableEntry {

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            int startPc;

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            int endPc;

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            int handlerPc;

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            int catchType;

        }

        public class Exceptions extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            int numberOfExceptions;

            @BoundList(size = "numberOfExceptions")
            ClassCpInfo[] exceptionIndexTable;

        }

    }

    public class SourceFile extends AttributeInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int sourceFileIndex;

        public String getName() {
            return ((Utf8CpInfo) constantPool[sourceFileIndex]).getStringValue();
        }

    }

    public class FieldInfo {

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int accessFlags;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int nameIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int descriptorIndex;

        @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
        private int attributesCount;

        //        @BoundList(size = "attributesCount", selectFrom = @Choices(prefixSize = 16, defaultType = AnyAttributeInfo.class))
        @BoundList(size = "attributesCount", type = AttributeInfo.class)
        private AttributeInfo[] attributes;

        public String getDescriptor() {
            return ((Utf8CpInfo) constantPool[descriptorIndex]).getStringValue();
        }

        private class ConstantValue extends AttributeInfo {

            @BoundNumber(size = "16", byteOrder = ByteOrder.BigEndian)
            private int constantValueIndex;

        }

    }

}
