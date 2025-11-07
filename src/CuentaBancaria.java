public class CuentaBancaria {

    private String nroCuenta;
    private double saldo;
    private String titular;

    public CuentaBancaria(String nroCuenta, double saldo, String titular) {
        this.nroCuenta = nroCuenta;
        this.saldo = saldo;
        this.titular = titular;
    }

    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
        }
    }

    public boolean extraer(double monto) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    public void transferir(double monto, CuentaBancaria destino) {
        if (destino == null) return;

        if (extraer(monto)) {
            destino.depositar(monto);
        }
    }

    public double mostrarSaldo() {
        return saldo;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {
        return "CuentaBancaria{" +
                "nroCuenta='" + nroCuenta + '\'' +
                ", saldo=" + saldo +
                ", titular='" + titular + '\'' +
                '}';
    }

    // Getters y setters

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
}
