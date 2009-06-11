/**
 * Copyright (C) 2003 - 2008
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.topologies.GBestTopology;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.StandardParticle;
import net.sourceforge.cilib.type.types.Real;

import net.sourceforge.cilib.util.VectorUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Gary Pampara
 */
public class DiameterTest {

    @Test
    public void simpleDiameter() {
        Particle p1 = new StandardParticle();
        Particle p2 = new StandardParticle();

        p1.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(0.0, 0.0));
        p2.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(2.0, 2.0));

        Topology<Particle> topology = new GBestTopology<Particle>();
        topology.add(p1);
        topology.add(p2);

        PSO pso = new PSO();
        pso.setTopology(topology);

        Measurement m = new Diameter();

        Assert.assertEquals(new Real(Math.sqrt(8)), m.getValue(pso));
    }

    @Test
    public void complexDiameter() {
        Particle p1 = new StandardParticle();
        Particle p2 = new StandardParticle();
        Particle p3 = new StandardParticle();
        Particle p4 = new StandardParticle();

        p1.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(0.0, 0.0));
        p2.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(1.0, 1.0));
        p3.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(1.5, 1.5));
        p4.getProperties().put(EntityType.CANDIDATE_SOLUTION, VectorUtils.create(2.0, 2.0));

        Topology<Particle> topology = new GBestTopology<Particle>();
        topology.add(p1);
        topology.add(p2);
        topology.add(p3);
        topology.add(p4);

        PSO pso = new PSO();
        pso.setTopology(topology);

        Measurement m = new Diameter();

        Assert.assertEquals(new Real(Math.sqrt(8)), m.getValue(pso));
    }

}
