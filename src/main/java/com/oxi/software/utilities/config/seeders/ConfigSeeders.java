package com.oxi.software.utilities.config.seeders;

import com.oxi.software.entities.DocumentType;
import com.oxi.software.entities.IndividualType;
import com.oxi.software.entities.RolType;
import com.oxi.software.entities.Unit;
import com.oxi.software.repository.DocumentTypeRepository;
import com.oxi.software.repository.IndividualTypeRepository;
import com.oxi.software.repository.RolTypeRepository;
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
    private final RolTypeRepository rolTypeRepository;

    public ConfigSeeders(DocumentTypeRepository documentTypeRepository, IndividualTypeRepository individualTypeRepository , UnitRepository unitRepository, RolTypeRepository rolTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
        this.individualTypeRepository = individualTypeRepository;
        this.unitRepository = unitRepository;
        this.rolTypeRepository = rolTypeRepository;
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
        if (rolTypeRepository.count() == 0){
            rolTypeRepository.saveAll(getRole());
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
                .acronym("LB")
                .build();

        Unit unit2 = Unit.builder()
                .unitType("Kilo")
                .acronym("KG")
                .build();

        Unit unit3 = Unit.builder()
                .unitType("Metro Cúbico")
                .acronym("M3")
                .build();
        Unit unit4 = Unit.builder()
                .unitType("Metros Cuadrados")
                .acronym("M2")
                .build();


        List<Unit> unitList = new ArrayList<>();
        unitList.add(unit1);
        unitList.add(unit2);
        unitList.add(unit3);
        unitList.add(unit4);
        return  unitList;
    }

    // Seeder for Individual Types

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

    // Seeder for Rol
    private  List<RolType> getRole() {
        RolType vendedor = RolType.builder()
                .name("Vendedor")
                .description("Vendedor")
                .build();

        RolType gerente = RolType.builder()
                .name("Gerente")
                .description("Gerente")
                .build();

        RolType usuario = RolType.builder()
                .name("Usuario")
                .description("Usuario")
                .build();

        RolType domiciliario = RolType.builder()
                .name("Domiciliario")
                .description("Domiciliario")
                .build();

//      List<PermissionEntity> rolesPermisos = this.getPermisos();
//        List<String> permisosPermitidosCordinator = Arrays.asList("CREATE", "READ", "UPDATE");
//        List<PermissionEntity> rolesPermisosCordinator = rolesPermisos.stream()
//                .filter(permissionEntity -> permissionEntity.getName().equals("CREATE")|| permissionEntity.getName().equals("UPDATE")|| permissionEntity.getName().equals("READ"))
//                .collect(Collectors.toList());

        List<RolType> roleList = new ArrayList<>();
        roleList.add(vendedor);
        roleList.add(usuario);
        roleList.add(domiciliario);
        roleList.add(gerente);
        return roleList;
    }


}