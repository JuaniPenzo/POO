import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa una cuenta bancaria simple con saldo y registro de
 * movimientos (débitos y créditos)
 */
public class CuentaBancaria {

    private String nroCuenta;
    private double saldo;
    private String titular;
    // movimientos internos de la cuenta (DEBE/HABER)
    private List<Registro> movimientos = new ArrayList<>();

    public CuentaBancaria(String nroCuenta, double saldo, String titular) {
        this.nroCuenta = nroCuenta;
        this.saldo = saldo;
        this.titular = titular;
    }

    // Depósito genérico (HABER)
    public void depositar(double monto) {
        depositar(monto, "Depósito");
    }

    // Depósito con descripción personalizada
    public void depositar(double monto, String descripcion) {
        if (monto > 0) {
            saldo += monto;
            // registrar movimiento
            Registro r = new Registro(movimientos.size() + 1, new Date(), "HABER", descripcion, monto, null, null,
                    null);
            movimientos.add(r);
        }
    }

    // Depósito vinculando entidad (socio/empleado/clase)
    public void depositar(double monto, String descripcion, Socio socio, Empleado empleado, Clase clase) {
        if (monto > 0) {
            saldo += monto;
            Registro r = new Registro(movimientos.size() + 1, new Date(), "HABER", descripcion, monto, socio, empleado,
                    clase);
            movimientos.add(r);
        }
    }

    // Extracción genérica (DEBE)
    public boolean extraer(double monto) {
        return extraer(monto, "Extracción");
    }

    // Extracción con descripción
    public boolean extraer(double monto, String descripcion) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            Registro r = new Registro(movimientos.size() + 1, new Date(), "DEBE", descripcion, monto, null, null, null);
            movimientos.add(r);
            return true;
        }
        return false;
    }

    // Extracción vinculando entidad (socio/empleado/clase)
    public boolean extraer(double monto, String descripcion, Socio socio, Empleado empleado, Clase clase) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
            Registro r = new Registro(movimientos.size() + 1, new Date(), "DEBE", descripcion, monto, socio, empleado,
                    clase);
            movimientos.add(r);
            return true;
        }
        return false;
    }

    // Extracción forzada (permite saldo negativo)
    public boolean extraerForzado(double monto, String descripcion, Socio socio, Empleado empleado, Clase clase) {
        if (monto > 0) {
            saldo -= monto;
            Registro r = new Registro(movimientos.size() + 1, new Date(), "DEBE", descripcion, monto, socio, empleado,
                    clase);
            movimientos.add(r);
            return true;
        }
        return false;
    }

    /**
     * Agrega un movimiento existente a la lista sin modificar el saldo.
     * Útil para cargar datos históricos.
     */
    public void agregarMovimiento(Registro r) {
        if (r != null) {
            movimientos.add(r);
        }
    }

    public void transferir(double monto, CuentaBancaria destino) {
        if (destino == null)
            return;

        if (extraer(monto, "Transferencia saliente a " + (destino != null ? destino.getNroCuenta() : "?"))) {
            destino.depositar(monto, "Transferencia entrante desde " + this.getNroCuenta());
        }
    }

    public boolean registrarPagoSueldo(Empleado e, Gimnasio g) {
        if (e == null || g == null) {
            return false;
        }
        boolean pagoRealizado = e.cobrarSueldo(this);
        if (!pagoRealizado) {
            return false;
        }
        Registro registro = new Registro(
                g.registros.size() + 1,
                new Date(),
                "DEBE",
                "Pago de sueldo a " + e.getNombre() + " " + e.getApellido(),
                e.getSueldo(),
                null,
                e,
                null);
        g.registros.add(registro);
        g.guardarEstadoCompleto();
        return true;
    }

    public boolean registrarPagoSocio(Socio s, double monto, Gimnasio g) {
        if (s == null || g == null) {
            return false;
        }
        boolean pagoRealizado = s.pagarCuota(monto, this);
        if (!pagoRealizado) {
            return false;
        }
        Registro registro = new Registro(
                g.registros.size() + 1,
                new Date(),
                "HABER",
                "Pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                monto,
                s,
                null,
                null);
        g.registros.add(registro);
        g.guardarEstadoCompleto();
        return true;
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

    // Movimientos
    public List<Registro> getMovimientos() {
        return Collections.unmodifiableList(movimientos);
    }

    public List<Registro> getMovimientosPorMes(int month, int year) {
        List<Registro> result = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (Registro r : movimientos) {
            Date f = r.getFecha();
            if (f == null)
                continue;
            cal.setTime(f);
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH) + 1; // 1-12
            if ((year == 0 || y == year) && (month == 0 || m == month))
                result.add(r);
        }
        return result;
    }

    /**
     * Calcula totales (haber, debe, neto) para el mes/año indicado. month 1-12;
     * month==0 -> todo el año
     * Retorna un array de 3 posiciones: [0]=haber, [1]=debe, [2]=neto(haber-debe)
     */
    public double[] calcularResumenMes(int month, int year) {
        double haber = 0.0;
        double debe = 0.0;
        List<Registro> lista = getMovimientosPorMes(month, year);
        for (Registro r : lista) {
            String tipo = r.getTipo();
            double monto = r.getMonto();
            if (tipo == null)
                continue;

            // Clasificar según tipo de operación
            if (tipo.equalsIgnoreCase("HABER") || tipo.equals("PAGO_CUOTA") || tipo.equals("DEPOSITO")) {
                haber += monto;
            } else if (tipo.equalsIgnoreCase("DEBE") || tipo.equals("PAGO_SUELDO") || tipo.equals("ANULACION_PAGO")
                    || tipo.equals("EXTRACCION")) {
                debe += monto;
            }
        }
        return new double[] { haber, debe, haber - debe };
    }

    // Getters y setters

    public String getNroCuenta() {
        return nroCuenta;
    }

    public void setNroCuenta(String nroCuenta) {
        this.nroCuenta = nroCuenta;
    }
}
