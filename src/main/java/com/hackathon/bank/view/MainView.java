package com.hackathon.bank.view;

import com.hackathon.bank.components.TransactionEditor;
import com.hackathon.bank.domain.Transaction;
import com.hackathon.bank.repository.TransactionRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route
public class MainView extends VerticalLayout {
    private final TransactionRepository transactionRepository;
    final TextField filter = new TextField("", "Type to filter");
    private final Button addTransaction = new Button("Add new");
    private final TextField deviceCodeField = new TextField("Device Code");
    private final TextField currencyField = new TextField("Currency");
    private final TextField resultLabel = new TextField("Total amount");
    private final DatePicker datePicker = new DatePicker("Date");
    private final Button calculateButton = new Button("Calculate Total Amount");
    private final TextField deviceCodeInput = new TextField("Device Code");
    private final DatePicker dateInput = new DatePicker("Date");
    private final Button calculateTotalInKGSButton = new Button("Calculate Total in KGS");
    private final TextField totalAmountInKGSLabel = new TextField("Total Amount in KGS");
    private final Button clearList = new Button("Clear");
    private final HorizontalLayout toolBar = new HorizontalLayout(filter, addTransaction);
    private final TransactionEditor transactionEditor;
    private Grid<Transaction> grid;

    @Autowired
    public MainView(TransactionRepository transactionRepository, TransactionEditor transactionEditor) {
        this.transactionRepository = transactionRepository;
        this.transactionEditor = transactionEditor;
        grid = new Grid<>(Transaction.class);
        grid.setColumns("id", "deviceCode", "operDate", "amount", "curr", "cardNumber");

        FileBuffer fileBuffer = new FileBuffer();
        Upload upload = new Upload(fileBuffer);

        upload.addSucceededListener(event -> {
            File file = fileBuffer.getFileData().getFile();
            try {
                InputStream inputStream = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0); // Assuming data is on the first sheet

                for (Row row : sheet) {
                    String deviceCode;
                    Cell deviceCodeCell = row.getCell(1);
                    if (deviceCodeCell != null) {
                        if (deviceCodeCell.getCellType() == CellType.NUMERIC) {
                            // Если ячейка содержит числовое значение, преобразуем его в строку
                            deviceCode = String.valueOf((int) deviceCodeCell.getNumericCellValue());
                        } else {
                            // Иначе считаем, что ячейка содержит строку
                            deviceCode = deviceCodeCell.getStringCellValue();
                        }
                    } else {
                        deviceCode = ""; // или любое другое значение по умолчанию, если ячейка пустая
                    }

                    LocalDate operDate;
                    Cell operDateCell = row.getCell(2);
                    if (operDateCell != null) {
                        if (operDateCell.getCellType() == CellType.NUMERIC) {
                            // Если ячейка содержит числовое значение, преобразуем его в LocalDate
                            double operDateValue = operDateCell.getNumericCellValue();
                            LocalDate baseDate;
                            baseDate = LocalDate.of(1900, 1, 1); // для формата с 1900 годом

                            operDate = baseDate.plusDays((long) operDateValue);
                        } else {
                            // Иначе считаем, что ячейка содержит строку, попытаемся разобрать её как дату
                            try {
                                operDate = LocalDate.parse(operDateCell.getStringCellValue());
                            } catch (DateTimeParseException e) {
                                operDate = null; // Если разбор не удался, устанавливаем значение null
                            }
                        }
                    } else {
                        operDate = null; // или любое другое значение по умолчанию, если ячейка пустая
                    }


                    double amount;
                    Cell amountCell = row.getCell(4);
                    if (amountCell != null) {
                        amount = amountCell.getNumericCellValue();
                    } else {
                        amount = 0; // или любое другое значение по умолчанию, если ячейка пустая
                    }

                    String currency;
                    Cell currencyCell = row.getCell(3);
                    if (currencyCell != null) {
                        if (currencyCell.getCellType() == CellType.NUMERIC) {
                            // Если ячейка содержит числовое значение, преобразуем его в строку
                            currency = String.valueOf((int) currencyCell.getNumericCellValue());
                        } else {
                            // Иначе считаем, что ячейка содержит строку
                            currency = currencyCell.getStringCellValue();
                        }
                    } else {
                        currency = ""; // или любое другое значение по умолчанию, если ячейка пустая
                    }

                    String cardNumber;
                    Cell cardNumberCell = row.getCell(5);
                    if (cardNumberCell != null) {
                        if (cardNumberCell.getCellType() == CellType.NUMERIC) {
                            // Если ячейка содержит числовое значение, преобразуем его в строку
                            cardNumber = String.valueOf((long) cardNumberCell.getNumericCellValue());
                        } else {
                            // Иначе считаем, что ячейка содержит строку
                            cardNumber = cardNumberCell.getStringCellValue();
                        }
                    } else {
                        cardNumber = ""; // или любое другое значение по умолчанию, если ячейка пустая
                    }


                    // Create Transaction object and save to repository or display
                    Transaction transaction = new Transaction(deviceCode, operDate, amount, currency, cardNumber);
                    transactionRepository.save(transaction);
                }
                showTransactions("");
                Notification.show("Excel file uploaded and parsed successfully", 3000, Notification.Position.TOP_CENTER);
            } catch (Exception e) {
                e.printStackTrace();
                Notification.show("Error uploading and parsing Excel file", 3000, Notification.Position.TOP_CENTER);
            }
        });


        add(toolBar, grid, transactionEditor);
        add(upload, clearList);

        clearList.addClickListener(e -> {
            transactionRepository.deleteAll();
            showTransactions("");
        });

        HorizontalLayout totalAmountLayout = new HorizontalLayout(deviceCodeField, currencyField, datePicker, resultLabel, calculateButton);

        // Create a HorizontalLayout to hold the buttons horizontally
        HorizontalLayout totalAmountLayoutKGS = new HorizontalLayout(deviceCodeInput, dateInput, totalAmountInKGSLabel, calculateTotalInKGSButton);
        totalAmountLayout.setSpacing(true); // Optionally, add spacing between components

        add(totalAmountLayout);
        add(totalAmountLayoutKGS);

        add(transactionEditor);

        calculateTotalInKGSButton.addClickListener(e -> {
            String deviceCode = deviceCodeInput.getValue();
            LocalDate date = dateInput.getValue();

            if (deviceCode.isEmpty() || date == null) {
                Notification.show("Please fill in all fields");
            } else {
                BigDecimal totalAmountInKGS = calculateTotalAmountInKGS(deviceCode, date);
                totalAmountInKGSLabel.setValue(String.valueOf(totalAmountInKGS));
            }
        });

        calculateButton.addClickListener(e -> {
            String deviceCode = deviceCodeField.getValue();
            String currency = currencyField.getValue();
            LocalDate date = datePicker.getValue();

            if (deviceCode.isEmpty() || currency.isEmpty() || date == null) {
                resultLabel.setValue("Please fill in all fields");
            } else {
                BigDecimal totalAmount = calculateTotalAmountForDeviceCodeCurrencyAndDate(deviceCode, currency, date);
                resultLabel.setValue(String.valueOf(totalAmount));
            }
        });

        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> showTransactions(e.getValue()));

        grid.asSingleSelect().addValueChangeListener(e -> {
            transactionEditor.editTransaction(e.getValue());
        });

        addTransaction.addClickListener(e -> transactionEditor.editTransaction(new Transaction()));


        transactionEditor.setChangeHandler(() -> {
            transactionEditor.setVisible(false);
            showTransactions(filter.getValue());
        });

        showTransactions("");


    }


    private BigDecimal calculateTotalAmountForDeviceCodeCurrencyAndDate(String deviceCode, String currency, LocalDate date) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Transaction> transactions = transactionRepository.findByDeviceCodeAndCurrAndOperDate(deviceCode, currency, date);
        for (Transaction transaction : transactions) {
            totalAmount = totalAmount.add(transaction.getAmount());
        }
        return totalAmount;
    }

    private BigDecimal calculateTotalAmountInKGS(String deviceCode, LocalDate date) {
        BigDecimal totalAmountInKGS = BigDecimal.ZERO;

        // Получаем все транзакции для указанного девайскода и даты
        List<Transaction> transactions = transactionRepository.findByDeviceCodeAndOperDate(deviceCode, date);

        // Перебираем каждую транзакцию и конвертируем ее сумму в сомы
        for (Transaction transaction : transactions) {
            BigDecimal amountInKGS = convertToKGS(transaction.getAmount(), transaction.getCurr());
            totalAmountInKGS = totalAmountInKGS.add(amountInKGS);
        }

        return totalAmountInKGS;
    }

    private BigDecimal convertToKGS(BigDecimal amount, String currency) {
        // Устанавливаем курсы обмена для каждой валюты
        BigDecimal rubToKGS = new BigDecimal("1.01"); // 1 RUB = 1.01 KGS
        BigDecimal usdToKGS = new BigDecimal("89");    // 1 USD = 89 KGS
        BigDecimal kztToKGS = new BigDecimal("5");     // 1 KZT = 5 KGS

        // Выполняем конвертацию в сомы (KGS) в зависимости от валюты
        switch (currency) {
            case "RUB":
                return amount.multiply(rubToKGS);
            case "USD":
                return amount.multiply(usdToKGS);
            case "KZT":
                return amount.multiply(kztToKGS);
            case "KGS":
                return amount;
            default:
                return BigDecimal.ZERO;
        }
    }

    private void showTransactions(String name) {
        if (name.isEmpty()) {
            grid.setItems(transactionRepository.findAll());
        } else {
            grid.setItems(transactionRepository.findByCurr(name));
        }
    }
}
