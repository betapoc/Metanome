/*
 * Copyright 2014 by the Metanome project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_potsdam.hpi.metanome.frontend.server;

import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_types.FunctionalDependencyAlgorithm;
import de.uni_potsdam.hpi.metanome.algorithm_integration.algorithm_types.InclusionDependencyAlgorithm;
import de.uni_potsdam.hpi.metanome.results_db.Algorithm;
import de.uni_potsdam.hpi.metanome.results_db.EntityStorageException;
import de.uni_potsdam.hpi.metanome.results_db.HibernateUtil;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link de.uni_potsdam.hpi.metanome.frontend.server.AlgorithmServiceImpl}
 * 
 * @author Jakob Zwiener
 */
public class AlgorithmServiceImplTest {

  /**
   * Test method for {@link AlgorithmServiceImpl#listAvailableAlgorithmFiles()}
   */
  @Test
  public void testListAvailableAlgorithmFiles() {
    // Setup
    AlgorithmServiceImpl service = new AlgorithmServiceImpl();

    // Expected values
    // Execute functionality
    String[] algos = service.listAvailableAlgorithmFiles();

    // Check result
    assertTrue(algos.length > 0);
  }

  /**
   * Test method for {@link AlgorithmServiceImpl#deleteAlgorithm(de.uni_potsdam.hpi.metanome.results_db.Algorithm)}
   */
  @Test
  public void testDeleteAlgorithm() throws EntityStorageException {
    // Setup
    HibernateUtil.clear();

    AlgorithmServiceImpl service = new AlgorithmServiceImpl();

    String fileName = "someFileName.jar";

    Algorithm expectedAlgorithm = new Algorithm(fileName);
    expectedAlgorithm.setInd(true);
    expectedAlgorithm.store();

    // Check precondition
    Algorithm actualAlgorithm = Algorithm.retrieve(fileName);
    assertEquals(expectedAlgorithm, actualAlgorithm);

    // Execute functionality
    service.deleteAlgorithm(expectedAlgorithm);

    // Check result
    assertNull(Algorithm.retrieve(fileName));

    // Cleanup
    HibernateUtil.clear();
  }

  /**
   * Test method for
   * {@link de.uni_potsdam.hpi.metanome.frontend.server.AlgorithmServiceImpl#listAlgorithms(Class)}
   * <p/>
   * When no interface is specified all stored algorithms should be retrieved by the service.
   */
  @Test
  public void testListAlgorithms() throws EntityStorageException {
    // Setup
    HibernateUtil.clear();

    AlgorithmServiceImpl finderService = new AlgorithmServiceImpl();

    // Expected values
    Algorithm[] expectedAlgorithms =
        {new Algorithm("some file name 1"), new Algorithm("some file name 2"),
            new Algorithm("some file name 3")};
    for (Algorithm algorithm : expectedAlgorithms) {
      algorithm.store();
    }

    // Execute functionality
    // Finds algorithms of all or no interfaces
    List<Algorithm> actualAlgorithms = finderService.listAlgorithms(null);

    // Check result
    assertThat(actualAlgorithms,
        IsIterableContainingInAnyOrder.containsInAnyOrder(expectedAlgorithms));

    // Cleanup
    HibernateUtil.clear();
  }

  /**
   * Test method for
   * {@link de.uni_potsdam.hpi.metanome.frontend.server.AlgorithmServiceImpl#listAlgorithms(Class)}
   * <p/>
   * When no interface is specified all stored algorithms should be retrieved by the service.
   */
  @Test
  public void testAddAlgorithm() throws EntityStorageException {
    // Setup
    HibernateUtil.clear();

    AlgorithmServiceImpl finderService = new AlgorithmServiceImpl();

    // Execute functionality: add an IND algorithm
    Algorithm algorithm = new Algorithm("someFileName.jar");
    algorithm.setInd(true);
    finderService.addAlgorithm(algorithm);

    // Check result
    assertTrue(finderService.listAllAlgorithms().contains(algorithm));
    assertTrue(finderService.listAlgorithms(InclusionDependencyAlgorithm.class).contains(algorithm));
    assertFalse(finderService.listAlgorithms(FunctionalDependencyAlgorithm.class).contains(
        algorithm));

    // Cleanup
    HibernateUtil.clear();
  }

  /**
   * Test method for {@link AlgorithmServiceImpl#listUniqueColumnCombinationsAlgorithms()},
   * {@link AlgorithmServiceImpl#listInclusionDependencyAlgorithms()},
   * {@link AlgorithmServiceImpl#listFunctionalDependencyAlgorithms()} and
   * {@link AlgorithmServiceImpl#listBasicStatisticsAlgorithms()}
   * <p/>
   * Stored algorithms that implement certain interfaces should be retrievable by the service.
   */
  @Test
  public void testListAlgorithmFileNamesInterface() throws EntityStorageException {
    // Setup
    HibernateUtil.clear();

    AlgorithmServiceImpl finderService = new AlgorithmServiceImpl();

    // Expected values
    Algorithm expectedIndAlgorithm = new Algorithm("ind algorithm").setInd(true).store();

    Algorithm expectedUccAlgorithm = new Algorithm("ucc algorithm").setUcc(true).store();

    Algorithm expectedFdAlgorithm = new Algorithm("fd algorithm").setFd(true).store();

    Algorithm expectedBasicStatAlgorithm =
        new Algorithm("basic stat algorithm").setBasicStat(true).store();

    Algorithm otherAlgorithm = new Algorithm("other algorithm").store();

    // Execute functionality
    List<Algorithm> actualIndAlgorithms = finderService.listInclusionDependencyAlgorithms();
    List<Algorithm> actualUccAlgorithms = finderService.listUniqueColumnCombinationsAlgorithms();
    List<Algorithm> actualFdAlgorithms = finderService.listFunctionalDependencyAlgorithms();
    List<Algorithm> actualBasicStatAlgorithms = finderService.listBasicStatisticsAlgorithms();
    List<Algorithm> actualAllAlgorithms = finderService.listAllAlgorithms();

    // Check result
    assertThat(actualIndAlgorithms,
        IsIterableContainingInAnyOrder.containsInAnyOrder(expectedIndAlgorithm));
    assertThat(actualUccAlgorithms,
        IsIterableContainingInAnyOrder.containsInAnyOrder(expectedUccAlgorithm));
    assertThat(actualFdAlgorithms,
        IsIterableContainingInAnyOrder.containsInAnyOrder(expectedFdAlgorithm));
    assertThat(actualBasicStatAlgorithms,
        IsIterableContainingInAnyOrder.containsInAnyOrder(expectedBasicStatAlgorithm));
    assertThat(actualAllAlgorithms, IsIterableContainingInAnyOrder.containsInAnyOrder(
        expectedIndAlgorithm, expectedUccAlgorithm, expectedFdAlgorithm,
        expectedBasicStatAlgorithm, otherAlgorithm));

    // Cleanup
    HibernateUtil.clear();
  }

}
