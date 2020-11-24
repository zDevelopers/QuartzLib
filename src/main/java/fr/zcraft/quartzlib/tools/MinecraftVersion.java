/*
 * Plugin UHCReloaded : Alliances
 *
 * Copyright ou © ou Copr. Amaury Carrade (2016)
 * Idées et réflexions : Alexandre Prokopowicz, Amaury Carrade, "Vayan".
 *
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions
 * de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA
 * sur le site "http://www.cecill.info".
 *
 * En contrepartie de l'accessibilité au code source et des droits de copie,
 * de modification et de redistribution accordés par cette licence, il n'est
 * offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
 * seule une responsabilité restreinte pèse sur l'auteur du programme,  le
 * titulaire des droits patrimoniaux et les concédants successifs.
 *
 * A cet égard  l'attention de l'utilisateur est attirée sur les risques
 * associés au chargement,  à l'utilisation,  à la modification et/ou au
 * développement et à la reproduction du logiciel par l'utilisateur étant
 * donné sa spécificité de logiciel libre, qui peut le rendre complexe à
 * manipuler et qui le réserve donc à des développeurs et des professionnels
 * avertis possédant  des  connaissances  informatiques approfondies.  Les
 * utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
 * logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement,
 * à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
 *
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez
 * pris connaissance de la licence CeCILL, et que vous en avez accepté les
 * termes.
 */

package fr.zcraft.quartzlib.tools;

import org.bukkit.Material;

/**
 * With some inspiration from Markus Lechner.
 */
public enum MinecraftVersion {
    VERSION_1_12_2_OR_OLDER,
    VERSION_1_13_X,
    VERSION_1_14_X_OR_NEWER,
    VERSION_ERROR;

    private static MinecraftVersion version = null;

    public static MinecraftVersion get() {
        if (version != null) {
            return version;
        }

        for (Material reg : Material.values()) {
            // I am looking at you Cauldron
            if (reg == null || reg.toString() == null || reg.toString().isEmpty()) {
                continue;
            }

            if (reg.toString().equalsIgnoreCase("SIGN_POST")) {
                version = MinecraftVersion.VERSION_1_12_2_OR_OLDER;
                break;
            } else if (reg.toString().equalsIgnoreCase("SIGN")) {
                version = MinecraftVersion.VERSION_1_13_X;
                break;
            } else if (reg.toString().equalsIgnoreCase("JUNGLE_SIGN")) {
                version = MinecraftVersion.VERSION_1_14_X_OR_NEWER;
                break;
            }
        }

        if (version == null) {
            version = MinecraftVersion.VERSION_ERROR;
        }

        return version;
    }
}
