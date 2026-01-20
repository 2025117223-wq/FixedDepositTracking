package Bean;

public class Bank {

    private Long bankId;  // Use Long for BIGINT
    private String bankName;
    private String bankAddress;
    private String bankPhone;

    // Constructor
    public Bank(Long bankId, String bankName, String bankAddress, String bankPhone) {
        this.bankId = bankId;  // BANKID (auto-generated)
        this.bankName = bankName;
        this.bankAddress = bankAddress;
        this.bankPhone = bankPhone;
    }

    // Getters and setters
    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankPhone() {
        return bankPhone;
    }

    public void setBankPhone(String bankPhone) {
        this.bankPhone = bankPhone;
    }
}
