public class Main {
    public static void main(String[] args) {
        System.out.println("Main iniciado");
        try {
            Gimnasio gimnasio = Gimnasio.iniciarSistemaDemo();
            System.out.println("Gimnasio creado: " + gimnasio.getNombre());
            // Abrir interfaz gráfica principal (grilla semanal)
            gimnasio.mostrarInterfazPrincipal();
            System.out.println("Main finalizado correctamente");
        } catch (Throwable t) {
            System.err.println("Excepción en Main:");
            t.printStackTrace();
        }
    }
}
