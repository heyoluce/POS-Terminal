package com.hackathon.bank.components;

import com.hackathon.bank.domain.Transaction;
import com.hackathon.bank.repository.TransactionRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;

@SpringComponent
@UIScope
public class TransactionEditor extends VerticalLayout implements KeyNotifier {
    private final TransactionRepository transactionRepository;
    private Transaction transaction;

    private TextField deviceCode = new TextField("Device code");
    private DatePicker operDate = new DatePicker("Operation date");

    private TextField amnt = new TextField("Amount");
    private TextField curr = new TextField("Curr");
    private TextField cardNumber = new TextField("Card number");

    @Autowired
    public TransactionEditor(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;


        add(deviceCode, operDate, curr, amnt, cardNumber);



        binder.bindInstanceFields(this);


        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editTransaction(transaction));
        setVisible(false);
    }

    public void editTransaction(Transaction transaction) {
        if (transaction == null) {
            setVisible(false);
            return;
        }

        if (transaction.getId()!=null) {
            this.transaction = transactionRepository.findById(transaction.getId()).orElse(transaction);
        } else {
            this.transaction = transaction;
        }
            binder.setBean(this.transaction);
        setVisible(true);

        deviceCode.focus();
    }

    private void delete() {
        transactionRepository.delete(transaction);
        changeHandler.onChange();
    }

    private void save() {
        transactionRepository.save(transaction);
        changeHandler.onChange();
    }

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Transaction> binder = new Binder<>(Transaction.class);
    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }


}
