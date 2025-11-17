public class Main {
    public static void main(String[] args) {
        System.out.println("Main iniciado");
        try {
            Gimnasio gimnasio = Gimnasio.iniciarSistemaDemo();
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
