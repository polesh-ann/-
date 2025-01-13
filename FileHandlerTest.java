package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipOutputStream;

class FileHandlerTest {

    @Test
    void testCalculateDiscountedPrice() {
        double originalPrice = 100.0;
        String discountFormula = "price * 0.8"; // 20% скидка

        double result = FileHandler.calculateDiscountedPrice(originalPrice, discountFormula);

        assertEquals(80.0, result, 0.01, "Цена со скидкой должна быть 80.0");
    }

    @Test
    void testCalculateDiscountedPrice_invalidFormula() {
        double originalPrice = 100.0;
        String discountFormula = "price * a"; // Неверная формула

        double result = FileHandler.calculateDiscountedPrice(originalPrice, discountFormula);

        assertEquals(originalPrice, result, "При ошибке в формуле цена должна остаться прежней");
    }


    @Test
    void testLoadVacsFromTxt() throws IOException {
        List<Vac> vacs = new ArrayList<>();
        BufferedReader mockReader = mock(BufferedReader.class);

        when(mockReader.readLine()).thenReturn("1,Model A,100.0,1500,2025-01-13");
        when(mockReader.readLine()).thenReturn(null); // EOF

        FileHandler.loadVacsFromTxt(vacs, "vacs.txt");

        assertEquals(1, vacs.size());
        assertEquals("Model A", vacs.get(0).getModel());
    }

    @Test
    void testSaveVacsToJson() throws IOException {
        List<Vac> vacs = Arrays.asList(new Vac("1", "Model A", 100.0, 1500, new Date()));

        ObjectMapper mockMapper = mock(ObjectMapper.class);
        doNothing().when(mockMapper).writeValue(any(File.class), any(List.class));

        FileHandler.saveVacsToJson(vacs, "vacs.json");

        verify(mockMapper, times(1)).writeValue(any(File.class), any(List.class));
    }


    @Test
    void testZipFile() throws IOException {
        File mockFile = mock(File.class);
        FileOutputStream mockFos = mock(FileOutputStream.class);
        ZipOutputStream mockZos = mock(ZipOutputStream.class);

        when(mockFile.exists()).thenReturn(true);

        FileHandler.zipFile("file.txt", "file.zip");

        verify(mockZos, times(1)).putNextEntry(any());
        verify(mockZos, times(1)).write(any(byte[].class), anyInt(), anyInt());
    }

    @Test
    void testSaveEncryptedJson() throws IOException {
        List<Vac> vacs = List.of(new Vac("1", "Model A", 100.0, 1500, new Date()));
        Files.write(Paths.get("encrypted.json"), "encrypted".getBytes());  // Пример простого сохранения

        FileHandler.saveEncryptedJson(vacs, "encrypted.json");

        // Дополнительно проверяем, что метод работает с правильными параметрами
    }

    @Test
    void testLoadEncryptedJson() throws IOException {
        Files.write(Paths.get("encrypted.json"), "encrypted".getBytes());

        List<Vac> vacs = FileHandler.loadEncryptedJson("encrypted.json");

        assertNotNull(vacs);
    }

    @Test
    void testSaveVacsToXml() {
        List<Vac> vacs = new ArrayList<>();
        vacs.add(new Vac("1", "Model A", 100.0, 1500, new Date()));

        try {
            FileHandler.saveVacsToXml(vacs, "vacs.xml");
        } catch (Exception e) {
            fail("Ошибка при сохранении в XML: " + e.getMessage());
        }
    }

    @Test
    void testLoadVacsFromXml() {
        try {
            List<Vac> vacs = FileHandler.loadVacsFromXml("vacs.xml");
            assertNotNull(vacs, "Список пылесосов не должен быть пустым");
        } catch (Exception e) {
            fail("Ошибка при загрузке из XML: " + e.getMessage());
        }
    }
}

