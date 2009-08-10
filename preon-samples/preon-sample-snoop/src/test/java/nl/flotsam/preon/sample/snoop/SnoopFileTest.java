package nl.flotsam.preon.sample.snoop;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.sample.snoop.SnoopFile.DatalinkType;
import nl.flotsam.preon.sample.snoop.SnoopFile.PacketRecord;

import org.junit.Test;


public class SnoopFileTest {

	@Test
	public void shouldBeAbleToReadPoo() throws FileNotFoundException, DecodingException, IOException {
		File file = new File(getBaseDir(), "src/test/resources/poo.cap");
		assertTrue(file.exists());
		Codec<SnoopFile> codec = Codecs.create(SnoopFile.class);
		SnoopFile snoopFile = Codecs.decode(codec, file);
		assertNotNull(snoopFile.getHeader());
		assertNotNull(snoopFile.getRecords());
		assertEquals(2, snoopFile.getHeader().getVersionNumber());
		assertEquals(DatalinkType.ETHERNET, snoopFile.getHeader().getDatalinkType());
		for (PacketRecord record: snoopFile.getRecords()) {
			System.out.println(record.getTimestampSeconds() + " : " + record.getTimestampMicroseconds());
		}
	}
	
	private File getBaseDir() {
		if (System.getProperty("basedir") != null) {
			return new File(System.getProperty("basedir"));
		} else {
			return new File(System.getProperty("user.dir"));
		}
	}
	
}
