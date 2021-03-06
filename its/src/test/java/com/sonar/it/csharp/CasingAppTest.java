/*
 * SonarSource :: C# :: ITs :: Plugin
 * Copyright (C) 2011-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.it.csharp;

import com.sonar.it.shared.TestUtils;
import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.ScannerForMSBuild;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonarqube.ws.Issues;

import static com.sonar.it.csharp.Tests.getComponent;
import static com.sonar.it.csharp.Tests.getIssues;
import static com.sonar.it.csharp.Tests.getMeasureAsInt;
import static org.assertj.core.api.Assertions.assertThat;

public class CasingAppTest {

  @Rule
  public TemporaryFolder temp = TestUtils.createTempFolder();

  @ClassRule
  public static final Orchestrator orchestrator = Tests.ORCHESTRATOR;

  @Before
  public void init() {
    TestUtils.reset(orchestrator);
  }

  @Test
  public void class1_should_have_metrics_and_issues() throws IOException {
    Path projectDir = Tests.projectDir(temp, "CasingApp");

    ScannerForMSBuild beginStep = TestUtils.createBeginStep("CasingApp", projectDir, "CasingApp");

    orchestrator.executeBuild(beginStep);

    TestUtils.runMSBuild(orchestrator, projectDir, "/t:Rebuild");

    orchestrator.executeBuild(TestUtils.createEndStep(projectDir));

    String class1ComponentKey = "CasingApp:SRC/Class1.cs";

    assertThat(getComponent(class1ComponentKey)).isNotNull();

    assertThat(getMeasureAsInt(class1ComponentKey, "files")).isEqualTo(1);
    assertThat(getMeasureAsInt(class1ComponentKey, "lines")).isEqualTo(10);
    assertThat(getMeasureAsInt(class1ComponentKey, "ncloc")).isEqualTo(9);

    List<Issues.Issue> issues = getIssues(class1ComponentKey);
    assertThat(issues).hasSize(1);
  }
}
