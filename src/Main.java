public class Main {
    public static void main(String[] args) {
        System.out.println("Main iniciado");
        try {
            // Inicializar gimnasio vacío
            Gimnasio gimnasio = new Gimnasio("Gimnasio Olavarría", 123456789, "San Martín 123", "Buenos Aires");

            // Cargar datos persistidos
            gimnasio.cargarDatos();

            // Iniciar la interfaz gráfica principal
            InterfazGimnasio interfaz = new InterfazGimnasio(gimnasio);
            interfaz.mostrarInterfazPrincipal();
            System.out.println("Main finalizado correctamente");
        } catch (Throwable t) {
            System.err.println("Excepción en Main:");
            t.printStackTrace();
        }
    }
}
