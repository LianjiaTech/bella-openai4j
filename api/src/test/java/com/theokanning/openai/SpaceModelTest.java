package com.theokanning.openai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.space.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceModelTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void createSpaceRequestSerialization() throws Exception {
        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .spaceName("Test Space")
                .spaceDescription("A description")
                .spaceCode("custom-code")
                .ownerUid("owner-001")
                .ownerName("Owner")
                .userId(100L)
                .tenantId("t1")
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertEquals("Test Space", map.get("spaceName"));
        assertEquals("A description", map.get("spaceDescription"));
        assertEquals("custom-code", map.get("spaceCode"));
        assertEquals("owner-001", map.get("ownerUid"));
        assertEquals("Owner", map.get("ownerName"));
        assertEquals(100, map.get("userId"));
        assertEquals("t1", map.get("tenantId"));
    }

    @Test
    void updateSpaceNameRequestSerialization() throws Exception {
        UpdateSpaceNameRequest request = UpdateSpaceNameRequest.builder()
                .spaceCode("space-001")
                .spaceName("New Name")
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertEquals("space-001", map.get("spaceCode"));
        assertEquals("New Name", map.get("spaceName"));
    }

    @Test
    void createSpaceRequestNullFieldsOmitted() throws Exception {
        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .spaceName("Minimal")
                .ownerUid("u1")
                .ownerName("U")
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertFalse(map.containsKey("spaceDescription"));
        assertFalse(map.containsKey("spaceCode"));
        assertFalse(map.containsKey("email"));
        assertFalse(map.containsKey("optionalInfo"));
    }

    @Test
    void spaceOperatorContextFields() throws Exception {
        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .spaceName("S")
                .ownerUid("u")
                .ownerName("U")
                .userId(1L)
                .userName("admin")
                .email("admin@test.com")
                .tenantId("t1")
                .source("web")
                .sourceId("src-1")
                .managerAk("ak-123")
                .optionalInfo(Collections.singletonMap("key", "value"))
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertEquals(1, map.get("userId"));
        assertEquals("admin", map.get("userName"));
        assertEquals("admin@test.com", map.get("email"));
        assertEquals("t1", map.get("tenantId"));
        assertEquals("web", map.get("source"));
        assertEquals("src-1", map.get("sourceId"));
        assertEquals("ak-123", map.get("managerAk"));
        assertNotNull(map.get("optionalInfo"));
    }

    @Test
    void removeMemberRequestSerialization() throws Exception {
        RemoveMemberRequest request = RemoveMemberRequest.builder()
                .memberUid("user-001")
                .spaceCode("space-001")
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertEquals("user-001", map.get("memberUid"));
        assertEquals("space-001", map.get("spaceCode"));
    }

    @Test
    void createRoleRequestWithRoles() throws Exception {
        CreateRoleRequest request = CreateRoleRequest.builder()
                .spaceCode("space-001")
                .roles(Arrays.asList(
                        CreateRoleDetail.builder().roleCode("admin").roleName("Admin").roleDesc("Administrator").build(),
                        CreateRoleDetail.builder().roleCode("viewer").roleName("Viewer").build()
                ))
                .build();

        String json = mapper.writeValueAsString(request);
        Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        assertEquals("space-001", map.get("spaceCode"));
        List<?> roles = (List<?>) map.get("roles");
        assertEquals(2, roles.size());

        CreateRoleRequest deserialized = mapper.readValue(json, CreateRoleRequest.class);
        assertEquals(2, deserialized.getRoles().size());
        assertEquals("admin", deserialized.getRoles().get(0).getRoleCode());
        assertNull(deserialized.getRoles().get(1).getRoleDesc());
    }

    @Test
    void createMemberRequestWithMembers() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .spaceCode("space-001")
                .roleCode("member")
                .members(Arrays.asList(
                        MemberItem.builder().memberUid("u1").memberName("User 1").build(),
                        MemberItem.builder().memberUid("u2").memberName("User 2").build()
                ))
                .build();

        String json = mapper.writeValueAsString(request);
        CreateMemberRequest deserialized = mapper.readValue(json, CreateMemberRequest.class);

        assertEquals("space-001", deserialized.getSpaceCode());
        assertEquals("member", deserialized.getRoleCode());
        assertEquals(2, deserialized.getMembers().size());
        assertEquals("u1", deserialized.getMembers().get(0).getMemberUid());
    }

    @Test
    void emptyMembersList() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .spaceCode("space-001")
                .roleCode("member")
                .members(Collections.emptyList())
                .build();

        String json = mapper.writeValueAsString(request);
        CreateMemberRequest deserialized = mapper.readValue(json, CreateMemberRequest.class);

        assertNotNull(deserialized.getMembers());
        assertTrue(deserialized.getMembers().isEmpty());
    }

    @Test
    void spaceDeserialization() throws Exception {
        String json = "{\"spaceCode\":\"s1\",\"spaceName\":\"Space One\",\"ownerUid\":\"o1\"}";
        Space space = mapper.readValue(json, Space.class);

        assertEquals("s1", space.getSpaceCode());
        assertEquals("Space One", space.getSpaceName());
        assertEquals("o1", space.getOwnerUid());
    }

    @Test
    void memberDeserialization() throws Exception {
        String json = "{\"spaceCode\":\"s1\",\"roleCode\":\"admin\",\"memberName\":\"Alice\",\"memberUid\":\"u1\"}";
        Member member = mapper.readValue(json, Member.class);

        assertEquals("s1", member.getSpaceCode());
        assertEquals("admin", member.getRoleCode());
        assertEquals("Alice", member.getMemberName());
        assertEquals("u1", member.getMemberUid());
    }

    @Test
    void roleWithSpaceDeserialization() throws Exception {
        String json = "{\"roleCode\":\"editor\",\"spaceCode\":\"s1\",\"spaceName\":\"My Space\"}";
        RoleWithSpace role = mapper.readValue(json, RoleWithSpace.class);

        assertEquals("editor", role.getRoleCode());
        assertEquals("s1", role.getSpaceCode());
        assertEquals("My Space", role.getSpaceName());
    }

    @Test
    void listDeserialization() throws Exception {
        String json = "[{\"spaceCode\":\"s1\",\"spaceName\":\"A\",\"ownerUid\":\"o1\"},{\"spaceCode\":\"s2\",\"spaceName\":\"B\",\"ownerUid\":\"o2\"}]";
        List<Space> spaces = mapper.readValue(json, new TypeReference<List<Space>>() {});

        assertEquals(2, spaces.size());
        assertEquals("s1", spaces.get(0).getSpaceCode());
        assertEquals("s2", spaces.get(1).getSpaceCode());
    }
}
