public class Main {
    public static void main(String[] args) {
        System.out.println("Main iniciado");
        try {
            Gimnasio gimnasio = Gimnasio.iniciarSistemaDemo();
            System.out.println("Gimnasio creado: " + gimnasio.getNombre());
            gimnasio.mostrarMenuPrincipal();
            System.out.println("Main finalizado correctamente");
        } catch (Throwable t) {
            System.err.println("Excepción en Main:");
            t.printStackTrace();
        }
    }
}
