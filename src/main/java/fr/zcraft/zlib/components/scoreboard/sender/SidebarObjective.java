/*
 * Copyright or © or Copr. ZLib contributors (2015)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.zcraft.zlib.components.scoreboard.sender;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Represents an objective behind a {@link fr.zcraft.zlib.components.scoreboard.Sidebar Sidebar}.
 */
public class SidebarObjective
{
    private String name;
    private String displayName;

    private Map<String, Integer> scores = new HashMap<>();
    private Set<UUID> receivers = new HashSet<>();


    /**
     * Constructs a new sidebar objective.
     *
     * @param name The name of the objective.
     * @param displayName The display name of this objective (i.e. the title of the sidebar).
     */
    public SidebarObjective(String name, String displayName)
    {
        if(name == null)
            name = UUID.randomUUID().getLeastSignificantBits() + "";

        this.name = name.substring(0, Math.min(32, name.length()));
        this.displayName = displayName != null ? displayName.substring(0, Math.min(32, displayName.length())) : "";
    }

    /**
     * Constructs a new sidebar objective with a random name.
     *
     * @param displayName The display name of this objective (i.e. the title of the sidebar).
     */
    public SidebarObjective(String displayName)
    {
        this(null, displayName);
    }

    /**
     * Constructs a new sidebar objective with a random name and without display name.
     */
    public SidebarObjective()
    {
        this(null);
    }


    /**
     * Sets a score.
     *
     * @param name The name of the score.
     * @param score The score associated.
     * @return {@code true} if a previous score was overwritten by this one.
     */
    public boolean setScore(String name, Integer score)
    {
        Validate.notNull(name, "The score name cannot be null!");
        Validate.notNull(score, "The score cannot be null!");

        return scores.put(name, score) != null;
    }

    /**
     * Sets the objective's display name (i.e. the title of the sidebar).
     *
     * @param displayName The new display name.
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Adds a receiver of this objective.
     *
     * @param id The player's UUID.
     */
    public void addReceiver(UUID id)
    {
        receivers.add(id);
    }

    /**
     * Removes a receiver from this objective.
     *
     * @param id The player's UUID.
     */
    public void removeReceiver(UUID id)
    {
        receivers.remove(id);
    }


    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Map<String, Integer> getScores()
    {
        return scores;
    }

    public Set<UUID> getReceivers()
    {
        return receivers;
    }
}
