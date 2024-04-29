import java.io.*;
import java.util.*;
import java.io.IOException;

public class DirectorioCiudades {
    private Map<String, List<String>> estadoCiudades;
    private String archivoCiudades;

    public DirectorioCiudades(String archivoCiudades) {
        this.archivoCiudades = archivoCiudades;
        estadoCiudades = new HashMap<>();
        cargarDatosDesdeArchivo();
    }

    public void agregarCiudad(String estado, String ciudad, int latitud, int longitud) {
        estadoCiudades.computeIfAbsent(estado, k -> new ArrayList<>()).add(ciudad +" " + estado + " " + latitud + " " + longitud);
        guardarDatosEnArchivo();
    }

    public void eliminarCiudad(int numeroCiudad) {
        List<String> todasLasCiudades = new ArrayList<>();
        for (List<String> ciudades : estadoCiudades.values()) {
            todasLasCiudades.addAll(ciudades);
        }
    
        if (numeroCiudad >= 1 && numeroCiudad <= todasLasCiudades.size()) {
            String ciudadAEliminar = todasLasCiudades.get(numeroCiudad - 1);
    
            for (List<String> ciudades : estadoCiudades.values()) {
                if (ciudades.contains(ciudadAEliminar)) {
                    ciudades.remove(ciudadAEliminar);
                    guardarDatosEnArchivo();
                    break;
                }
            }
            System.out.println("La ciudad " + ciudadAEliminar + " ha sido eliminada.");
        } else {
            System.out.println("Número de ciudad inválido.");
        }
    }
    

    public List<String> ciudadesEnEstado(String estado) {
        return estadoCiudades.getOrDefault(estado, new ArrayList<>());
    }

    public List<String> ciudadesEnRangoDeCoordenadas(int latMin, int latMax, int lonMin, int lonMax) {
        List<String> ciudadesEnRango = new ArrayList<>();
        for (List<String> ciudades : estadoCiudades.values()) {
            for (String ciudad : ciudades) {
                String[] partes = ciudad.split(" ");
                int latitud = Integer.parseInt(partes[2]);
                int longitud = Integer.parseInt(partes[3]);
                if (latitud >= latMin && latitud <= latMax && longitud >= lonMin && longitud <= lonMax) {
                    ciudadesEnRango.add(ciudad);
                    System.out.println(ciudad);
                }
            }
        }
        return ciudadesEnRango;
    }

    public void imprimirCiudadesNumeradas() {
            int numero = 0;
            for (Map.Entry<String, List<String>> entry : estadoCiudades.entrySet()) {
                List<String> ciudades = entry.getValue();
                for (String ciudad : ciudades) {
                    System.out.println(numero + ". " + ciudad);
                    numero++;
                }
            }
        }

    public void imprimirTodasLasCiudades() {
        for (Map.Entry<String, List<String>> entry : estadoCiudades.entrySet()) {
            List<String> ciudades = entry.getValue();
            for (String ciudad : ciudades) {
                System.out.println(ciudad);
            }
        }
    }

    private void cargarDatosDesdeArchivo() {
        try (Scanner scanner = new Scanner(new File(archivoCiudades))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(" ");
                String estado = partes[1];
                String ciudad = partes[0];
                int latitud = Integer.parseInt(partes[2]);
                int longitud = Integer.parseInt(partes[3]);
                agregarCiudad(estado, ciudad, latitud, longitud);
            }
        } catch (FileNotFoundException e) {
            System.err.println("El archivo " + archivoCiudades + " no existe. Se creará uno nuevo al guardar datos.");
        }
    }

    private void guardarDatosEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivoCiudades))) {
            for (List<String> ciudades : estadoCiudades.values()) {
                for (String ciudad : ciudades) {
                    writer.println(ciudad);
                }
            }

        } catch (IOException e) {
            System.err.println("Error al guardar datos en " + archivoCiudades + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
    Scanner m = new Scanner(System.in);
    String fileName = "ciudades.txt";
    DirectorioCiudades directorio = new DirectorioCiudades(fileName);
    BuscarCiudades.imprimirCantidadDeCiudades(fileName);

    while (true) {
        System.out.println("Seleccione una acción:");
        System.out.println("1. Agregar ciudad");
        System.out.println("2. Eliminar ciudad");
        System.out.println("3. Ciudades en estado");
        System.out.println("4. Ciudades en rango de coordenadas");
        System.out.println("5. Imprimir todas las ciudades");
        System.out.println("0. Salir");

        int opcion = m.nextInt();
        m.nextLine(); // Consumir el salto de línea

        switch (opcion) {
            case 1:
                Colors.println("¿Cuál es el nombre de la ciudad? ", Colors.HIGH_INTENSITY);
                String estado = m.nextLine();
                Colors.println("¿Cuál es el estado en el que se encuentra la ciudad? ", Colors.HIGH_INTENSITY);
                String ciudad = m.nextLine();

                String error = Colors.HIGH_INTENSITY + "Ingresa una opción válida.";
                String longitud = "¿Cuál es su coordenada X?";
                String latitud = "¿Cuál es su coordenada Y?";
                int max = Integer.MAX_VALUE;
                int min = Integer.MIN_VALUE;

                int equis1 = BuscarCiudades.getInt(longitud, error, min, max);

                int equis3 = BuscarCiudades.getInt(latitud, error, min, max);
                
                directorio.agregarCiudad(estado, ciudad, equis1, equis3);
                break;

            case 2:
                System.out.println("Ciudades disponibles: ");
                directorio.imprimirCiudadesNumeradas();
                System.out.print("Ingrese el número de la ciudad a eliminar: ");
                int numeroCiudad = m.nextInt();
                directorio.eliminarCiudad(numeroCiudad);
                break;

            case 3:
                System.out.print("Ingrese el estado: ");
                estado = m.nextLine();
                List<String> ciudadesEnEstado = directorio.ciudadesEnEstado(estado);
                System.out.println("Ciudades en " + estado + ": " + ciudadesEnEstado);
                break;

            case 4:
                List<Ciudad> ciudades = new ArrayList<>();
                
                try {
                    BuscarCiudades.leerArchivo(ciudades, fileName);
                } catch (IOException e) {
                    Colors.println("Error al leer el archivo: " + e.getMessage(), Colors.RED);
                }
                
                Ciudad.encontrarMaxMin(ciudades);
                BuscarCiudades.obtenerCoordenadas(ciudades);
                break;

            case 5:
                directorio.imprimirTodasLasCiudades();
                break;

            case 0:
                System.out.println("¡Hasta luego!");
                System.exit(0);
            default:
                System.out.println("Opción no válida. Intente nuevamente.");
        }
    }
    }
}
