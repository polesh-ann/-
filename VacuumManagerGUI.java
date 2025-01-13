package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacuumManagerGUI extends JFrame {
    private List<Vac> vacs;
    private DefaultTableModel tableModel;
    private JTable table;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public VacuumManagerGUI() {
        vacs = new VacList<Vac>().getVacs();
        setupUI();
    }

    private void setupUI() {
        setTitle("Менеджер пылесосов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);

        String[] columns = {"ID", "Модель", "Цена", "Макс. мощность", "Дата выпуска"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton saveJsonButton = new JButton("Сохранить в JSON");
        JButton loadJsonButton = new JButton("Загрузить из JSON");
        JButton saveXmlButton = new JButton("Сохранить в XML");
        JButton loadXmlButton = new JButton("Загрузить из XML");
        JButton calculateDiscountButton = new JButton("Посчитать скидку");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveJsonButton);
        buttonPanel.add(loadJsonButton);
        buttonPanel.add(saveXmlButton);
        buttonPanel.add(loadXmlButton);
        buttonPanel.add(calculateDiscountButton);
        add(buttonPanel, BorderLayout.SOUTH);


        addButton.addActionListener(this::showAddDialog);
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedVac());
        saveJsonButton.addActionListener(e -> saveToJsonAsync());
        loadJsonButton.addActionListener(e -> loadFromJsonAsync());
        saveXmlButton.addActionListener(e -> saveToXmlAsync());
        loadXmlButton.addActionListener(e -> loadFromXmlAsync());
        calculateDiscountButton.addActionListener(this::calculateDiscount);
    }

    private void calculateDiscount(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пылесос для подсчета скидки");
            return;
        }

        Vac selectedVac = vacs.get(selectedRow);


        String discountInput = JOptionPane.showInputDialog(this, "Введите процент скидки (например, 10 для 10%):");

        if (discountInput != null && !discountInput.trim().isEmpty()) {
            try {
                double discountPercentage = Double.parseDouble(discountInput);
                if (discountPercentage < 0 || discountPercentage > 100) {
                    JOptionPane.showMessageDialog(this, "Скидка должна быть в пределах от 0 до 100%");
                    return;
                }
                double discountedPrice = selectedVac.getPrice() * (1 - discountPercentage / 100);

                selectedVac.setPrice(discountedPrice);

                updateTable();
                JOptionPane.showMessageDialog(this, "Цена после скидки: " + discountedPrice);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка в введенных данных. Введите число.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка в вводе скидки.");
        }
    }

    private void showAddDialog(ActionEvent e) {
        JDialog dialog = new JDialog(this, "Добавить пылесос", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField idField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField powerField = new JTextField();
        JTextField dateField = new JTextField();

        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Модель:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Цена:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Макс. мощность:"));
        dialog.add(powerField);
        dialog.add(new JLabel("Дата выпуска (yyyy-MM-dd):"));
        dialog.add(dateField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(ev -> {
            try {
                Vac vac = new Builders.VacBuilder()
                        .setId(idField.getText())
                        .setModel(modelField.getText())
                        .setPrice(Double.parseDouble(priceField.getText()))
                        .setMaxPower(Double.parseDouble(powerField.getText()))
                        .setReleaseDate(dateFormat.parse(dateField.getText()))
                        .build();
                vacs.add(vac);
                updateTable();
                dialog.dispose();
            } catch (ParseException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка в введенных данных");
            }
        });

        dialog.add(saveButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пылесос для редактирования");
            return;
        }

        Vac selectedVac = vacs.get(selectedRow);
        JDialog dialog = new JDialog(this, "Редактировать пылесос", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField modelField = new JTextField(selectedVac.getModel());
        JTextField priceField = new JTextField(String.valueOf(selectedVac.getPrice()));
        JTextField powerField = new JTextField(String.valueOf(selectedVac.getMaxPower()));
        JTextField dateField = new JTextField(dateFormat.format(selectedVac.getReleaseDate()));

        dialog.add(new JLabel("Модель:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Цена:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Макс. мощность:"));
        dialog.add(powerField);
        dialog.add(new JLabel("Дата выпуска (yyyy-MM-dd):"));
        dialog.add(dateField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                selectedVac.setModel(modelField.getText());
                selectedVac.setPrice(Double.parseDouble(priceField.getText()));
                selectedVac.setMaxPower(Double.parseDouble(powerField.getText()));
                selectedVac.setReleaseDate(dateFormat.parse(dateField.getText()));
                updateTable();
                dialog.dispose();
            } catch (ParseException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка в введенных данных");
            }
        });

        dialog.add(saveButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedVac() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите пылесос для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить этот пылесос?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            vacs.remove(selectedRow);

            tableModel.removeRow(selectedRow);
            updateTable();
        }
    }


    private void saveToJsonAsync() {
        executor.execute(() -> {
            FileHandler.saveVacsToJson(vacs, "vacs.json");
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Данные сохранены в JSON")
            );
        });
    }

    private void loadFromJsonAsync() {
        executor.execute(() -> {
            vacs = FileHandler.loadVacsFromJson("vacs.json");
            SwingUtilities.invokeLater(() -> {
                updateTable();
                JOptionPane.showMessageDialog(this, "Данные загружены из JSON");
            });
        });
    }

    private void saveToXmlAsync() {
        executor.execute(() -> {
            FileHandler.saveVacsToXml(vacs, "vacs.xml");
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Данные сохранены в XML")
            );
        });
    }

    private void loadFromXmlAsync() {
        executor.execute(() -> {
            vacs = FileHandler.loadVacsFromXml("vacs.xml");
            SwingUtilities.invokeLater(() -> {
                updateTable();
                JOptionPane.showMessageDialog(this, "Данные загружены из XML");
            });
        });
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Vac vac : vacs) {
            tableModel.addRow(new Object[]{
                    vac.getVacId(),
                    vac.getModel(),
                    vac.getPrice(),
                    vac.getMaxPower(),
                    dateFormat.format(vac.getReleaseDate())
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VacuumManagerGUI().setVisible(true);
        });
    }
}
