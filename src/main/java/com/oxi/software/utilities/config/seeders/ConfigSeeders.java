package com.oxi.software.utilities.config.seeders;

import com.oxi.software.entities.DocumentType;
import com.oxi.software.entities.IndividualType;
import com.oxi.software.entities.Unit;
import com.oxi.software.repository.DocumentTypeRepository;
import com.oxi.software.repository.IndividualTypeRepository;
import com.oxi.software.repository.UnitRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConfigSeeders implements CommandLineRunner {


    private final DocumentTypeRepository documentTypeRepository;
    private final UnitRepository unitRepository;
    private final IndividualTypeRepository individualTypeRepository;

    public ConfigSeeders(DocumentTypeRepository documentTypeRepository, IndividualTypeRepository individualTypeRepository ,UnitRepository unitRepository ) {
        this.documentTypeRepository = documentTypeRepository;
        this.individualTypeRepository = individualTypeRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        //Inserts data in the database
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
        DocumentType documentType1 = DocumentType.builder()
                .name("Cédula de ciudadanía")
                .acronym("C.C")
                .build();

        DocumentType documentType2 = DocumentType.builder()
                .name("Cédula de extranjería")
                .acronym("C.E")
                .build();

        DocumentType documentType3 = DocumentType.builder()
                .name("Número de Identificación Tributaria")
                .acronym("NIT")
                .build();


        List<DocumentType> documentTypeList = new ArrayList<>();
        documentTypeList.add(documentType1);
        documentTypeList.add(documentType2);
        documentTypeList.add(documentType3);
        return  documentTypeList;
    }

    // Seeder for unit types
    private List<Unit> getAllUnit() {
        Unit unit1 = Unit.builder()
                .unitType("Libra")
                .acronym("Lb")
                .build();

        Unit unit2 = Unit.builder()
                .unitType("Kilo")
                .acronym("Kg")
                .build();

        Unit unit3 = Unit.builder()
                .unitType("Metro Cúbico")
                .acronym("m3")
                .build();
        Unit unit4 = Unit.builder()
                .unitType("Metros Cuadrados")
                .acronym("m2")
                .build();


        List<Unit> unitList = new ArrayList<>();
        unitList.add(unit1);
        unitList.add(unit2);
        unitList.add(unit3);
        unitList.add(unit4);
        return  unitList;
    }

    // Seeder for

    private List<IndividualType> getAllIndividualType() {

        IndividualType individualType1 = IndividualType.builder()
                .name("Persona")
                .build();

        IndividualType individualType2 = IndividualType.builder()
                .name("Empresa")
                .build();

        List<IndividualType> individualTypeList = new ArrayList<>();
        individualTypeList.add(individualType1);
        individualTypeList.add(individualType2);

        return individualTypeList;
    }

//   //seeder persmisos
//    private  List<PermissionEntity> getPermisos() {
//        PermissionEntity create = PermissionEntity.builder()
//                .id(1L)
//                .name("CREATE")
//                .build();
//        PermissionEntity update = PermissionEntity.builder()
//                .id(2L)
//                .name("UPDATE")
//                .build();
//        PermissionEntity delete = PermissionEntity.builder()
//                .id(3L)
//                .name("DELETE")
//                .build();
//        PermissionEntity read = PermissionEntity.builder()
//                .id(4L)
//                .name("READ")
//                .build();
//        List<PermissionEntity> permissionEntityList=new ArrayList<>();
//        permissionEntityList.add(create);
//        permissionEntityList.add(update);
//        permissionEntityList.add(delete);
//        permissionEntityList.add(read);
//        return permissionEntityList;
//    }
//    // seeder roles
//    private  List<Role> getRole() {
//
//        List<PermissionEntity> rolesPermisos = this.getPermisos();
//
//
//
//        Role superAdmin = Role.builder()
//                .id(1L)
//                .name("SUPER ADMIN")
//                .description("can create, read and disable information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisos)
//                .build();
//
//        Role admin = Role.builder()
//                .id(2L)
//                .name("ADMIN")
//                .description("can create, read and disable information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisos)
//                .build();
//        //  List<String> permisosPermitidosCordinator = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosCordinator = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE")|| permissionEntity.getName().equals("UPDATE")|| permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());
//        Role coordinator = Role.builder()
//                .id(3L)
//                .name("COORDINADOR")
//                .description("can create, read and edit information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisosCordinator)
//                .build();
//        // List<String> permisosPermitidosInstructor = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosInstructor = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE")|| permissionEntity.getName().equals("UPDATE")|| permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());
//        Role instructor = Role.builder()
//                .id(4L)
//                .name("INSTRUCTOR")
//                .description("can create, read and edit information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisosInstructor)
//                .build();
//        // List<String> permisosPermitidosAprendiz = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosAprendiz = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE") || permissionEntity.getName().equals("UPDATE") || permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());
//        Role aprendiz = Role.builder()
//                .id(5L)
//                .name("APRENDIZ")
//                .description("can create, read and edit information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisosInstructor)
//                .build();
//        // List<String> permisosPermitidosSeguridad = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosSeguridad = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE") || permissionEntity.getName().equals("UPDATE") || permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());
//        Role seguridad = Role.builder()
//                .id(6L)
//                .name("SEGURIDAD")
//                .description("can create, read and disable information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisosInstructor)
//                .build();
//        // List<String> permisosPermitidosInvitados = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosInvitados = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE") || permissionEntity.getName().equals("UPDATE") || permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());
//        Role invitado = Role.builder()
//                .id(7L)
//                .name("INVITADO")
//                .description("can create, read and disable information of the system")
//                .createdAt(new Date())
//                .state(Boolean.TRUE)
//                .permissions(rolesPermisosInstructor)
//                .build();
//        List<Role> roleList = new ArrayList<>();
//        roleList.add(superAdmin);
//        roleList.add(admin);
//        roleList.add(coordinator);
//        roleList.add(instructor);
//        roleList.add(aprendiz);
//        roleList.add(seguridad);
//        roleList.add(invitado);
//        return roleList;
//    }


}