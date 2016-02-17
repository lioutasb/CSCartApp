package gr.plushost.prototypeapp.items;

import java.util.List;

/**
 * Created by billiout on 21/3/2015.
 */
public class CustomerOrderItem {
    private String order_id;
    private String display_id;
    private String email;
    private String firstname;
    private String lastname;
    private String date_added;
    private String currency;
    private List<CustomerOrderPriceInfoItem> price_infos;
    private CustomerOrderStatusItem order_status;
    private List<CustomerOrderProductItem> order_items;
    private AddressItem shipping_address;
    private AddressItem billing_address;
    private PaymentMethodItem payment_method;
    private ShippingMethodItem shipping_method;
    private String barcode_img_url;
    private String invoice_pdf_url;
    private String notes;
    private UserAddressItem shipping_address_2;
    private List<String> messages;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public UserAddressItem getBilling_address_2() {
        return billing_address_2;
    }

    public void setBilling_address_2(UserAddressItem billing_address_2) {
        this.billing_address_2 = billing_address_2;
    }

    public UserAddressItem getShipping_address_2() {
        return shipping_address_2;
    }

    public void setShipping_address_2(UserAddressItem shipping_address_2) {
        this.shipping_address_2 = shipping_address_2;
    }

    private UserAddressItem billing_address_2;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInvoice_pdf_url() {
        return invoice_pdf_url;
    }

    public void setInvoice_pdf_url(String invoice_pdf_url) {
        this.invoice_pdf_url = invoice_pdf_url;
    }

    public String getBarcode_img_url() {
        return barcode_img_url;
    }

    public void setBarcode_img_url(String barcode_img_url) {
        this.barcode_img_url = barcode_img_url;
    }

    public AddressItem getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(AddressItem shipping_address) {
        this.shipping_address = shipping_address;
    }

    public PaymentMethodItem getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethodItem payment_method) {
        this.payment_method = payment_method;
    }

    public AddressItem getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(AddressItem billing_address) {
        this.billing_address = billing_address;
    }

    public ShippingMethodItem getShipping_method() {
        return shipping_method;
    }

    public void setShipping_method(ShippingMethodItem shipping_method) {
        this.shipping_method = shipping_method;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDisplay_id() {
        return display_id;
    }

    public void setDisplay_id(String display_id) {
        this.display_id = display_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public List<CustomerOrderPriceInfoItem> getPrice_infos() {
        return price_infos;
    }

    public void setPrice_infos(List<CustomerOrderPriceInfoItem> price_infos) {
        this.price_infos = price_infos;
    }

    public CustomerOrderStatusItem getOrder_status() {
        return order_status;
    }

    public void setOrder_status(CustomerOrderStatusItem order_status) {
        this.order_status = order_status;
    }

    public List<CustomerOrderProductItem> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<CustomerOrderProductItem> order_items) {
        this.order_items = order_items;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
