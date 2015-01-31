package org.codehaus.preon;

public class InstanceFactoryFactory implements CodecFactoryFactory {
	
	CodecFactory codecFactoryInstance;
	
	InstanceFactoryFactory (CodecFactory codecFactoryInstance) {
		this.codecFactoryInstance = codecFactoryInstance;
	}

	public CodecFactory create(CodecFactory delegate) {
		return codecFactoryInstance;
	}

}
