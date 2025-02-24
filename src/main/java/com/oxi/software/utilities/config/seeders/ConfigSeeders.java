package com.oxi.software.utilities.config.seeders;

import com.oxi.software.entity.*;
import com.oxi.software.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConfigSeeders implements CommandLineRunner {


    private final DocumentTypeRepository documentTypeRepository;
    private final UnitRepository unitRepository;
    private final IndividualTypeRepository individualTypeRepository;
    private final RolTypeRepository rolTypeRepository;
    private final PermissionRepository permissionRepository;

    public ConfigSeeders(DocumentTypeRepository documentTypeRepository, IndividualTypeRepository individualTypeRepository , UnitRepository unitRepository, RolTypeRepository rolTypeRepository, PermissionRepository permissionRepository) {
        this.documentTypeRepository = documentTypeRepository;
        this.individualTypeRepository = individualTypeRepository;
        this.unitRepository = unitRepository;
        this.rolTypeRepository = rolTypeRepository;
        this.permissionRepository = permissionRepository;
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
}