/**
 * LabSET 2014
 */
package org.codehaus.preon.codec;

import static org.codehaus.preon.buffer.ByteOrder.BigEndian;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.Init;
import org.codehaus.preon.annotation.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hasnaer
 *
 */
public class InjectCodecDecoratorTest {

  private Codec<Team> codec;
  private byte[] buffer;

  @Before
  public void setUp() throws IOException {
    try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream)) {
      stream.writeInt(12);
      stream.writeInt(3);
      stream.writeInt(10);
      stream.writeInt(9);
      stream.writeInt(8);
      codec = Codecs.create(Team.class);
      buffer = byteStream.toByteArray();
    }
  }

  @Test
  public void testInjectDecorator() throws DecodingException {
    Team team = Codecs.decode(codec, buffer);
    Assert.assertEquals(12, team.coach);
    Assert.assertEquals(3, team.playersCount);
    Assert.assertEquals(3, team.players.length);
    Assert.assertEquals(12, team.players[0].personalCoach);
  }

  public static class Team {
    @BoundNumber(size = "32", byteOrder = BigEndian)
    int coach;
    @BoundNumber(size = "32", byteOrder = BigEndian)
    int playersCount;
    @BoundList(size = "playersCount")
    Player[] players;
  }

  public static class Player {
    @BoundNumber(size = "32", byteOrder = BigEndian)
    int ID;
    @Inject(name="coach")
    int personalCoach;
    @Init
    private void init() {
      System.out.println(String.format("%s - %s", ID, personalCoach));
    }
  }
}