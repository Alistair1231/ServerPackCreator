package de.griefed.serverpackcreator.modscanner;

import com.electronwill.nightconfig.toml.TomlParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.griefed.serverpackcreator.ApplicationProperties;
import de.griefed.serverpackcreator.modscanning.AnnotationScanner;
import de.griefed.serverpackcreator.modscanning.FabricScanner;
import de.griefed.serverpackcreator.modscanning.ModScanner;
import de.griefed.serverpackcreator.modscanning.QuiltScanner;
import de.griefed.serverpackcreator.modscanning.TomlScanner;
import de.griefed.serverpackcreator.utilities.common.Utilities;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModScannerTest {

  ModScanner modScanner;

  ModScannerTest() {
    ObjectMapper objectMapper =
        new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    Utilities utilities = new Utilities(new ApplicationProperties());
    modScanner = new ModScanner(
        new AnnotationScanner(objectMapper, utilities),
        new FabricScanner(objectMapper, utilities),
        new QuiltScanner(objectMapper, utilities),
        new TomlScanner(new TomlParser()));
  }

  @Test
  void tomlTest() {
    Collection<File> files =
        FileUtils.listFiles(
            new File("backend/test/resources/forge_tests/mods"), new String[] {"jar"}, true);

    List<File> excluded = new ArrayList<>(modScanner.tomls().scan(files));

    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/aaaaa.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/bbbbb.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/ccccc.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/ddddd.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/fffff.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/ggggg.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/hhhhh.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/iiiii.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/jjjjj.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/kkkkk.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/lllll.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/nnnnn.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/ppppp.jar")));

    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/qqqqq.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/rrrrr.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/testmod.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/uuuuu.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/vvvvv.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/wwwww.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/xxxxx.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/yyyyy.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_tests/mods/zzzzz.jar")));
  }

  @Test
  void fabricTest() {
    Collection<File> files =
        FileUtils.listFiles(
            new File("backend/test/resources/fabric_tests/mods"), new String[] {"jar"}, true);

    List<File> excluded = new ArrayList<>(modScanner.fabric().scan(files));

    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/aaaaa.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/bbbbb.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/ccccc.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/ddddd.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/eeeee.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/fffff.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/ggggg.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/hhhhh.jar")));

    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/iiiii.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/jjjjj.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/kkkkk.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/lllll.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/mmmmm.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/nnnnn.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/ooooo.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/ppppp.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/qqqqq.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/fabric_tests/mods/testmod.jar")));
  }

  @Test
  void quiltTest() {
    Collection<File> files =
        FileUtils.listFiles(
            new File("backend/test/resources/quilt_tests/mods"), new String[] {"jar"}, true);

    List<File> excluded = new ArrayList<>(modScanner.quilt().scan(files));

    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/quilt_tests/mods/aaaaa.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/quilt_tests/mods/bbbbb.jar")));

    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/quilt_tests/mods/testmod.jar")));
  }

  @Test
  void annotationTest() {
    Collection<File> files =
        FileUtils.listFiles(
            new File("backend/test/resources/forge_old/mods"), new String[] {"jar"}, true);

    List<File> excluded = new ArrayList<>(modScanner.annotations().scan(files));

    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/aaaaa.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/bbbbb.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/ccccc.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/ddddd.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/eeeee.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/fffff.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/ggggg.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/hhhhh.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/iiiii.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/jjjjj.jar")));
    Assertions.assertTrue(
        excluded.contains(new File("backend/test/resources/forge_old/mods/kkkkk.jar")));

    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/lllll.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/mmmmm.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/nnnnn.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/ooooo.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/ppppp.jar")));
    Assertions.assertFalse(
        excluded.contains(new File("backend/test/resources/forge_old/mods/qqqqq.jar")));
  }
}