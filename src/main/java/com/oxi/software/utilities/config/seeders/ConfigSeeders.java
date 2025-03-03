package com.oxi.software.utilities.config.seeders;

import com.oxi.software.entity.*;
import com.oxi.software.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConfigSeeders implements CommandLineRunner {


    private final DocumentTypeRepository documentTypeRepository;
    private final UnitRepository unitRepository;
    private final IndividualTypeRepository individualTypeRepository;
    private final RolTypeRepository rolTypeRepository;
    private final PermissionRepository permissionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final IndividualRepository individualRepository;
    private final PasswordEncoder passwordEncoder;

    public ConfigSeeders(DocumentTypeRepository documentTypeRepository, IndividualTypeRepository individualTypeRepository , UnitRepository unitRepository, RolTypeRepository rolTypeRepository, PermissionRepository permissionRepository, ProductRepository productRepository, UserRepository userRepository, IndividualRepository individualRepository, PasswordEncoder passwordEncoder) {
        this.documentTypeRepository = documentTypeRepository;
        this.individualTypeRepository = individualTypeRepository;
        this.unitRepository = unitRepository;
        this.rolTypeRepository = rolTypeRepository;
        this.permissionRepository = permissionRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.individualRepository = individualRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inserta los datos en la base de datos en el orden correcto
        if (permissionRepository.count() == 0) {
            permissionRepository.saveAll(getAllPermission());
        }
        if (rolTypeRepository.count() == 0) {
            rolTypeRepository.saveAll(getRole());
        }
        if (documentTypeRepository.count() == 0) {
            documentTypeRepository.saveAll(getDocumentTypeList());
        }
        if (unitRepository.count() == 0) {
            unitRepository.saveAll(getAllUnit());
        }
        if (individualTypeRepository.count() == 0) {
            individualTypeRepository.saveAll(getAllIndividualType());
        }
        if (productRepository.count() == 0){
            productRepository.saveAll(getProduct());
        }

        createAdminUser();
    }


    // Seeder for document types
    private List<DocumentType> getDocumentTypeList() {
        List<DocumentType> documentTypeList = new ArrayList<>();

        documentTypeList.add(DocumentType.builder()
                .name("Cédula de ciudadanía")
                .acronym("C.C")
                .build());

        documentTypeList.add(DocumentType.builder()
                .name("Número de Identificación Tributaria")
                .acronym("NIT")
                .build());

        documentTypeList.add(DocumentType.builder()
                .name("Cédula de extranjería")
                .acronym("C.E")
                .build());

        return  documentTypeList;
    }

    // Seeder for unit types
    private List<Unit> getAllUnit() {
        List<Unit> unitList = new ArrayList<>();

        unitList.add(Unit.builder()
                .unitType("Libra")
                .acronym("LB")
                .build());

        unitList.add(Unit.builder()
                .unitType("Kilo")
                .acronym("KG")
                .build());

        unitList.add(Unit.builder()
                .unitType("Metro Cúbico")
                .acronym("M3")
                .build());

        unitList.add(Unit.builder()
                .unitType("Metros Cuadrados")
                .acronym("M2")
                .build());

        return  unitList;
    }

    // Seeder for Individual Types

    private List<IndividualType> getAllIndividualType() {

        List<IndividualType> individualTypeList = new ArrayList<>();

        individualTypeList.add(IndividualType.builder()
                .name("Persona")
                .build());

        individualTypeList.add(IndividualType.builder()
                .name("Empresa")
                .build());

        return individualTypeList;
    }

    private List<Product> getProduct() {

        List<Product> ProductList = new ArrayList<>();

        ProductList.add(Product.builder()
                .name("Oxigeno")
                .state(true)
                .build());

        ProductList.add(Product.builder()
                .name("Árgon")
                .state(true)
                .build());

        return ProductList;
    }

   //seeder for permissions
   private List<Permission> getAllPermission() {
       List<Permission> permissionList = new ArrayList<>();

       permissionList.add(Permission.builder()
               .name("CREATE")
               .build());

       permissionList.add(Permission.builder()
               .name("UPDATE")
               .build());

       permissionList.add(Permission.builder()
               .name("DELETE")
               .build());

       permissionList.add(Permission.builder()
               .name("READ")
               .build());

       permissionList.add(Permission.builder()
               .name("REFACTOR")
               .build());

       return permissionList;
   }

    // Seeder para RolType con asignación de permisos usando findByName en PermissionRepository
    private List<RolType> getRole() {
        // Recupera los permisos por nombre; se lanza excepción si alguno no se encuentra.
        Permission create = permissionRepository.findByName("CREATE")
                .orElseThrow(() -> new RuntimeException("Permiso CREATE no encontrado"));
        Permission update = permissionRepository.findByName("UPDATE")
                .orElseThrow(() -> new RuntimeException("Permiso UPDATE no encontrado"));
        Permission delete = permissionRepository.findByName("DELETE")
                .orElseThrow(() -> new RuntimeException("Permiso DELETE no encontrado"));
        Permission read   = permissionRepository.findByName("READ")
                .orElseThrow(() -> new RuntimeException("Permiso READ no encontrado"));
        Permission refactor   = permissionRepository.findByName("REFACTOR")
                .orElseThrow(() -> new RuntimeException("Permiso Refactor no encontrado"));

        // Define el conjunto de permisos comunes (sin DELETE)
        Set<Permission> commonPermissions = new HashSet<>();
        commonPermissions.add(create);
        commonPermissions.add(update);
        commonPermissions.add(read);

        // Para el rol de Gerente se asignan todos los permisos (incluyendo DELETE)
        Set<Permission> adminPermissions = new HashSet<>(commonPermissions);
        adminPermissions.add(delete);

        Set<Permission> developerPermissions = new HashSet<>(adminPermissions);
        developerPermissions.add(refactor);

        List<RolType> roleList = new ArrayList<>();

        // Se crean los roles y se les asignan los permisos
        RolType vendedor = RolType.builder()
                .name("VENDEDOR")
                .description("Vendedor: puede leer, crear y actualizar")
                .build();
        vendedor.setPermissions(commonPermissions);

        RolType cliente = RolType.builder()
                .name("CLIENTE")
                .description("Cliente: puede leer, crear y actualizar")
                .build();
        cliente.setPermissions(commonPermissions);

        RolType domiciliario = RolType.builder()
                .name("DOMICILIARIO")
                .description("Domiciliario: puede leer, crear y actualizar")
                .build();
        domiciliario.setPermissions(commonPermissions);

        RolType gerente = RolType.builder()
                .name("GERENTE")
                .description("Gerente: puede leer, crear, eliminar y actualizar")
                .build();
        gerente.setPermissions(adminPermissions);

        RolType desarrollador = RolType.builder()
                .name("DESARROLLADOR")
                .description("Desarrollador: tiene todos los permisos")
                .build();
        desarrollador.setPermissions(developerPermissions);

        roleList.add(vendedor);
        roleList.add(cliente);
        roleList.add(domiciliario);
        roleList.add(gerente);
        roleList.add(desarrollador);

        return roleList;
    }

    private void createAdminUser() {
        String adminEmail = "admin@oxi.com";
        String vendorEmail = "vendor@oxi.com";
        String deliveryEmail = "domiciliario@oxi.com";

        if (!userRepository.existsByUsername(adminEmail)) {
            // Obtener tipos requeridos
            IndividualType adminType = individualTypeRepository.findById(1L)
                    .orElseGet(() -> individualTypeRepository.save(
                            IndividualType.builder()
                                    .name("Administrador")
                                    .build()));

            DocumentType cedula = documentTypeRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("DocumentType Cédula no encontrado"));

            RolType gerenteRole = rolTypeRepository.findById(4L)
                    .orElseThrow(() -> new RuntimeException("Rol GERENTE no encontrado"));

            RolType vendedorRole = rolTypeRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Rol VENDEDOR no encontrado"));

            RolType domicilarioRole = rolTypeRepository.findById(3L)
                    .orElseThrow(() -> new RuntimeException("Rol DOMICILIAIRO no encontrado"));


            // Crear User PRIMERO
            User adminUser = User.builder()
                    .state(true)
                    .username(adminEmail)
                    .password(passwordEncoder.encode("Admin1234"))
                    .rolType(gerenteRole)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();

            // Crear Individual y asociarlo al User
            Individual adminIndividual = Individual.builder()
                    .name("Administrador del Sistema")
                    .email(adminEmail)
                    .address("Sede Principal")
                    .document("1234567890")
                    .phone("3001234567")
                    .individualType(adminType)
                    .documentType(cedula)
                    .user(adminUser) // Relación bidireccional
                    .build();

            // Crear User PRIMERO
            User vendorUser = User.builder()
                    .state(true)
                    .username(vendorEmail)
                    .password(passwordEncoder.encode("Vendedor1234"))
                    .rolType(vendedorRole)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();


            // Crear Individual y asociarlo al User
            Individual vendorIndividual = Individual.builder()
                    .name("Administrador del Sistema")
                    .email(vendorEmail)
                    .address("Sede Principal")
                    .document("0987654321")
                    .phone("3001234567")
                    .individualType(adminType)
                    .documentType(cedula)
                    .user(vendorUser) // Relación bidireccional
                    .build();

            User deliveryUser = User.builder()
                    .state(true)
                    .username(deliveryEmail)
                    .password(passwordEncoder.encode("Domiciliario1234"))
                    .rolType(domicilarioRole)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .build();

            // Crear Individual y asociarlo al User
            Individual deliveryIndividual = Individual.builder()
                    .name("Administrador del Sistema")
                    .email(deliveryEmail)
                    .address("Sede Principal")
                    .document("789645123")
                    .phone("3001234567")
                    .individualType(adminType)
                    .documentType(cedula)
                    .user(deliveryUser) // Relación bidireccional
                    .build();


            // Asignar Individual al User
            adminUser.setIndividual(adminIndividual);
            vendorUser.setIndividual(vendorIndividual);
            deliveryUser.setIndividual(deliveryIndividual);

            // Guardar solo el User (se propagará al Individual por cascade)
            userRepository.save(adminUser); // No usar saveAndFlush
            userRepository.save(vendorUser);
            userRepository.save(deliveryUser);
        }
    }
}
