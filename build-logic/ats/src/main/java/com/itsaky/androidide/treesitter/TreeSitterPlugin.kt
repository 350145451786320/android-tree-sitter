/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.BasePluginAccessor
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.create
import java.util.Locale

/**
 * Marker plugin.
 *
 * @author Akash Yadav
 */
class TreeSitterPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.run {
      tasks.register("buildForHost", BuildForHostTask::class.java) {
        dependsOn(rootProject.tasks.getByName("buildTreeSitter"))
        libName = project.name
      }

      tasks.create("cleanHostBuild", type = Delete::class) {
        delete("src/main/cpp/host-build")
      }

      tasks.named("clean").configure { dependsOn("cleanHostBuild") }
      tasks.named("preBuild") { dependsOn("buildForHost") }

      val baseExtention = extensions.getByType(BaseExtension::class.java)
      val pluginType = if (plugins.hasPlugin(
          "com.android.application")
      ) AppPlugin::class.java else LibraryPlugin::class.java
      val dslServices = plugins.getPlugin(pluginType)
        .let { BasePluginAccessor.getDslServices(it) }

      @Suppress("DEPRECATION")
      val ndkPlatform = dslServices.sdkComponents.map {
        it.versionedNdkHandler(
          baseExtention.compileSdkVersion ?: throw kotlin.IllegalStateException(
            "compileSdkVersion not set in the android configuration"),
          baseExtention.ndkVersion,
          baseExtention.ndkPath).ndkPlatform.getOrThrow()
      }

      extensions.getByType(AndroidComponentsExtension::class.java).apply {
        onVariants { variant ->
          val variantName = variant.name.replaceFirstChar { name ->
            if (name.isLowerCase()) name.titlecase(
              Locale.ROOT) else name.toString()
          }

          tasks.register("generateDebugSymbols$variantName",
            GenerateDebugSymbolsTask::class.java) {

            dependsOn(tasks.getByName("merge${variantName}NativeLibs"))

            this.inputDirectory.set(
              variant.artifacts.get(SingleArtifact.MERGED_NATIVE_LIBS))
            this.outputDirectory.set(project.layout.buildDirectory.dir(
              "intermediates/ts_debug_symbols/${variant.name}/out"))
            this.ndkInfo.set(ndkPlatform.get().ndkInfo)
          }
        }
      }
    }
  }
}