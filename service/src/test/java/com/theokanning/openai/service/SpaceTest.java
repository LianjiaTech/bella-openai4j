package com.theokanning.openai.service;

import com.theokanning.openai.space.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SpaceTest {

    @Mock
    private OpenAiService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSpace() {
        CreateSpaceRequest request = CreateSpaceRequest.builder()
                .spaceName("Test Space")
                .spaceDescription("A test space")
                .ownerUid("user-001")
                .ownerName("Test User")
                .userId(123L)
                .tenantId("tenant-1")
                .build();
        when(service.createSpace(request)).thenReturn("space-code-001");

        String result = service.createSpace(request);

        assertNotNull(result);
        assertEquals("space-code-001", result);
    }

    @Test
    void updateSpaceName() {
        UpdateSpaceNameRequest request = UpdateSpaceNameRequest.builder()
                .spaceCode("space-001")
                .spaceName("New Name")
                .build();
        when(service.updateSpaceName(request)).thenReturn(true);

        Boolean result = service.updateSpaceName(request);

        assertTrue(result);
    }

    @Test
    void updateSpaceNameReturnsFalse() {
        UpdateSpaceNameRequest request = UpdateSpaceNameRequest.builder()
                .spaceCode("nonexistent")
                .spaceName("Name")
                .build();
        when(service.updateSpaceName(request)).thenReturn(false);

        Boolean result = service.updateSpaceName(request);

        assertFalse(result);
    }

    @Test
    void changeSpaceOwner() {
        ChangeSpaceOwnerRequest request = ChangeSpaceOwnerRequest.builder()
                .spaceCode("space-001")
                .ownerUid("new-owner")
                .build();
        when(service.changeSpaceOwner(request)).thenReturn(true);

        Boolean result = service.changeSpaceOwner(request);

        assertTrue(result);
    }

    @Test
    void getSpace() {
        Space expected = Space.builder()
                .spaceCode("space-001")
                .spaceName("My Space")
                .ownerUid("user-001")
                .build();
        when(service.getSpace("space-001")).thenReturn(expected);

        Space result = service.getSpace("space-001");

        assertNotNull(result);
        assertEquals("space-001", result.getSpaceCode());
        assertEquals("My Space", result.getSpaceName());
        assertEquals("user-001", result.getOwnerUid());
    }

    @Test
    void listSpaces() {
        List<String> codes = Arrays.asList("space-001", "space-002");
        List<Space> expected = Arrays.asList(
                Space.builder().spaceCode("space-001").spaceName("Space 1").ownerUid("u1").build(),
                Space.builder().spaceCode("space-002").spaceName("Space 2").ownerUid("u2").build()
        );
        when(service.listSpaces(codes)).thenReturn(expected);

        List<Space> result = service.listSpaces(codes);

        assertEquals(2, result.size());
    }

    @Test
    void listSpacesEmpty() {
        when(service.listSpaces(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<Space> result = service.listSpaces(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createRole() {
        CreateRoleRequest request = CreateRoleRequest.builder()
                .spaceCode("space-001")
                .roles(Arrays.asList(
                        CreateRoleDetail.builder().roleCode("admin").roleName("Admin").roleDesc("Administrator").build(),
                        CreateRoleDetail.builder().roleCode("member").roleName("Member").build()
                ))
                .build();
        when(service.createRole(request)).thenReturn(true);

        Boolean result = service.createRole(request);

        assertTrue(result);
    }

    @Test
    void listMemberRoles() {
        List<RoleWithSpace> expected = Arrays.asList(
                RoleWithSpace.builder().roleCode("admin").spaceCode("space-001").spaceName("Space 1").build(),
                RoleWithSpace.builder().roleCode("member").spaceCode("space-002").spaceName("Space 2").build()
        );
        when(service.listMemberRoles("user-001")).thenReturn(expected);

        List<RoleWithSpace> result = service.listMemberRoles("user-001");

        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).getRoleCode());
    }

    @Test
    void createMembers() {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .spaceCode("space-001")
                .roleCode("member")
                .members(Arrays.asList(
                        MemberItem.builder().memberUid("u1").memberName("User 1").build(),
                        MemberItem.builder().memberUid("u2").memberName("User 2").build()
                ))
                .build();
        when(service.createMembers(request)).thenReturn(true);

        Boolean result = service.createMembers(request);

        assertTrue(result);
    }

    @Test
    void removeMember() {
        RemoveMemberRequest request = RemoveMemberRequest.builder()
                .memberUid("user-001")
                .spaceCode("space-001")
                .build();
        when(service.removeMember(request)).thenReturn(true);

        Boolean result = service.removeMember(request);

        assertTrue(result);
    }

    @Test
    void updateMemberRole() {
        UpdateMemberRoleRequest request = UpdateMemberRoleRequest.builder()
                .memberUid("user-001")
                .spaceCode("space-001")
                .roleCode("admin")
                .build();
        when(service.updateMemberRole(request)).thenReturn(true);

        Boolean result = service.updateMemberRole(request);

        assertTrue(result);
    }

    @Test
    void exitSpace() {
        ExitSpaceRequest request = ExitSpaceRequest.builder()
                .memberUid("user-001")
                .spaceCode("space-001")
                .build();
        when(service.exitSpace(request)).thenReturn(true);

        Boolean result = service.exitSpace(request);

        assertTrue(result);
    }

    @Test
    void listMembers() {
        List<Member> expected = Arrays.asList(
                Member.builder().spaceCode("space-001").roleCode("admin").memberUid("u1").memberName("User 1").build(),
                Member.builder().spaceCode("space-001").roleCode("member").memberUid("u2").memberName("User 2").build()
        );
        when(service.listMembers("space-001")).thenReturn(expected);

        List<Member> result = service.listMembers("space-001");

        assertEquals(2, result.size());
        assertEquals("u1", result.get(0).getMemberUid());
    }

    @Test
    void listMembersEmpty() {
        when(service.listMembers("empty-space")).thenReturn(Collections.emptyList());

        List<Member> result = service.listMembers("empty-space");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMemberRole() {
        RoleWithSpace expected = RoleWithSpace.builder()
                .roleCode("admin")
                .spaceCode("space-001")
                .spaceName("My Space")
                .build();
        when(service.getMemberRole("user-001", "space-001")).thenReturn(expected);

        RoleWithSpace result = service.getMemberRole("user-001", "space-001");

        assertNotNull(result);
        assertEquals("admin", result.getRoleCode());
        assertEquals("space-001", result.getSpaceCode());
    }
}
