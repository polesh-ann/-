package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {
    private static final String ALGORITHM = "AES";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public static double calculateDiscountedPrice(double originalPrice, String discountFormula) {
        try {
            Expression expression = new ExpressionBuilder(discountFormula)
                    .variable("price")
                    .build();
            double discountedPrice = expression.evaluate();
            return discountedPrice;
        } catch (Exception e) {
            System.err.println("Ошибка при вычислении скидки: " + e.getMessage());
            return originalPrice;
        }
    }

    public static void saveVacsToTxt(List<Vac> vacs, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Vac vac : vacs) {
                writer.write(
                        vac.getVacId() +','+
                                vac.getModel() +','+
                                vac.getPrice() +','+
                                vac.getMaxPower() +','+
                                format.format(vac.getReleaseDate()) + '\n'
                );
            }
            System.out.println("Пылесосы сохранены в файле TXT.");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пылесосов в файл TXT: " + e.getMessage());
        }
    }

    public static void loadVacsFromTxt(List<Vac> vacs, String fileName) {
        vacs.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String id = parts[0].trim();
                String model = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                double maxPower = Double.parseDouble(parts[3].trim());
                Date releaseDate = format.parse(parts[4].trim());
                Vac vac = new Builders.VacBuilder()
                        .setId(id)
                        .setModel(model)
                        .setPrice(price)
                        .setMaxPower(maxPower)
                        .setReleaseDate(releaseDate)
                        .build();
                vacs.add(vac);
            }
            System.out.println("Пылесосы загружены из файла TXT.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Ошибка загрузки пылесосов из файла TXT: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка даты");
        }
    }

    public static void saveVacsToJson(List<Vac> vacs, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(fileName), vacs);
            System.out.println("Вакс сохранен в файл JSON.");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения в файл JSON: " + e.getMessage());
        }
    }

    public static List<Vac> loadVacsFromJson(String fileName) {
        try {
            return List.of(Singleton.getInstance().readJsonFromFile(fileName, Robot[].class));
        } catch (IOException e) {
            System.err.println("Ошибка загрузки из файла JSON: " + e.getMessage());
            return List.of();
        }
    }

    public static void saveVacsToXml(List<Vac> vacs, String fileName) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("vacs");
            doc.appendChild(rootElement);

            for (Vac vac : vacs) {
                Element vacElement = doc.createElement("vac");

                vacElement.appendChild(createElement(doc, "id", vac.getVacId()));
                vacElement.appendChild(createElement(doc, "model", vac.getModel()));
                vacElement.appendChild(createElement(doc, "price", String.valueOf(vac.getPrice())));
                vacElement.appendChild(createElement(doc, "maxPower", String.valueOf(vac.getMaxPower())));
                vacElement.appendChild(createElement(doc, "releaseDate", format.format(vac.getReleaseDate())));

                rootElement.appendChild(vacElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName));

            transformer.transform(source, result);
            System.out.println("Vacs сохранены в файл XML.");
        } catch (ParserConfigurationException | TransformerException e) {
            System.err.println("Ошибка сохранения в файл XML: " + e.getMessage());
        }
    }

    public static List<Vac> loadVacsFromXml(String filename) {
        List<Vac> vacs = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));

            NodeList vacNodes = document.getElementsByTagName("vacs");

            for (int i = 0; i < vacNodes.getLength(); i++) {
                Node node = vacNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String model = element.getElementsByTagName("model").item(0).getTextContent();
                    double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());
                    double maxPower = Double.parseDouble(element.getElementsByTagName("maxPower").item(0).getTextContent());
                    Date releaseDate = format.parse(element.getElementsByTagName("releaseDate").item(0).getTextContent());
                    Vac vac = new Vac(id, model, price, maxPower, releaseDate){};
                    vacs.add(vac);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vacs;
    }

    private static Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }

    public static void saveEncryptedJson(List<Vac> vacs, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(vacs);
            String encryptedJson = Base64.getEncoder().encodeToString(json.getBytes());

            Files.write(Paths.get(fileName), encryptedJson.getBytes());
            System.out.println("Зашифрованный JSON сохранен.");
        } catch (Exception e) {
            System.err.println("Ошибка сохранения зашифрованного JSON: " + e.getMessage());
        }
    }

    public static List<Vac> loadEncryptedJson(String fileName) {
        ObjectMapper mapper = Singleton.getInstance().getObjectMapper();
        try {
            String encryptedJson = new String(Files.readAllBytes(Paths.get(fileName)));
            String decryptedJson = new String(Base64.getDecoder().decode(encryptedJson.getBytes()));
            return List.of(mapper.readValue(decryptedJson, Robot[].class));
        } catch (Exception e) {
            System.err.println("Ошибка загрузки зашифрованного JSON: " + e.getMessage());
            return List.of();
        }
    }

    public static void zipFile(String fileName, String zipFileName) {
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            File fileToZip = new File(fileName);
            try (FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zos.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }
            System.out.println("Файл успешно заархивирован.");
        } catch (IOException e) {
            System.err.println("Ошибка архивирования файла: " + e.getMessage());
        }
    }
}

