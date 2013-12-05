/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2013 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.core.permission;

import org.junit.Before;
import org.junit.Test;
import org.sonar.core.persistence.AbstractDaoTestCase;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class GroupWithPermissionTemplateDaoTest extends AbstractDaoTestCase {

  private static final long TEMPLATE_ID = 50L;

  private PermissionTemplateDao dao;

  @Before
  public void setUp() {
    dao = new PermissionTemplateDao(getMyBatis());
  }

  @Test
  public void select_groups() throws Exception {
    setupData("groups_with_permissions");

    WithPermissionQuery query = WithPermissionQuery.builder().permission("user").build();
    List<GroupWithPermissionDto> result = dao.selectGroups(query, TEMPLATE_ID);

    assertThat(result).hasSize(4);

    GroupWithPermissionDto anyone = result.get(0);
    assertThat(anyone.getName()).isEqualTo("Anyone");
    assertThat(anyone.getPermission()).isNotNull();

    GroupWithPermissionDto group1 = result.get(1);
    assertThat(group1.getName()).isEqualTo("sonar-administrators");
    assertThat(group1.getPermission()).isNotNull();

    GroupWithPermissionDto group2 = result.get(2);
    assertThat(group2.getName()).isEqualTo("sonar-reviewers");
    assertThat(group2.getPermission()).isNull();

    GroupWithPermissionDto group3 = result.get(3);
    assertThat(group3.getName()).isEqualTo("sonar-users");
    assertThat(group3.getPermission()).isNotNull();
  }

  @Test
  public void anyone_group_is_not_returned_when_it_has_no_permission() throws Exception {
    setupData("groups_with_permissions");

    // Anyone group has not the permission 'admin', so it's not returned
    WithPermissionQuery query = WithPermissionQuery.builder().permission("admin").build();
    List<GroupWithPermissionDto> result = dao.selectGroups(query, TEMPLATE_ID);
    assertThat(result).hasSize(3);

    GroupWithPermissionDto group1 = result.get(0);
    assertThat(group1.getName()).isEqualTo("sonar-administrators");
    assertThat(group1.getPermission()).isNotNull();

    GroupWithPermissionDto group2 = result.get(1);
    assertThat(group2.getName()).isEqualTo("sonar-reviewers");
    assertThat(group2.getPermission()).isNull();

    GroupWithPermissionDto group3 = result.get(2);
    assertThat(group3.getName()).isEqualTo("sonar-users");
    assertThat(group3.getPermission()).isNull();
  }

  @Test
  public void search_by_groups_name() throws Exception {
    setupData("groups_with_permissions");

    List<GroupWithPermissionDto> result = dao.selectGroups(WithPermissionQuery.builder().permission("user").search("aDMini").build(), TEMPLATE_ID);
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("sonar-administrators");

    result = dao.selectGroups(WithPermissionQuery.builder().permission("user").search("sonar").build(), TEMPLATE_ID);
    assertThat(result).hasSize(3);
  }

  @Test
  public void search_groups_should_be_sorted_by_group_name() throws Exception {
    setupData("groups_with_permissions_should_be_sorted_by_group_name");

    List<GroupWithPermissionDto> result = dao.selectGroups(WithPermissionQuery.builder().permission("user").build(), TEMPLATE_ID);
    assertThat(result).hasSize(4);
    assertThat(result.get(0).getName()).isEqualTo("Anyone");
    assertThat(result.get(1).getName()).isEqualTo("sonar-administrators");
    assertThat(result.get(2).getName()).isEqualTo("sonar-reviewers");
    assertThat(result.get(3).getName()).isEqualTo("sonar-users");
  }

}
