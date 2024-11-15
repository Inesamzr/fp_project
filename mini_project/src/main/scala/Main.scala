import java.io._
import java.util.zip.{ZipEntry, ZipInputStream}
import org.json4s._

object Main {
  implicit val formats: Formats = DefaultFormats

  case class CVE(
    ID: String,
    Description: String,
    baseScore: Double,
    baseSeverity: String,
    exploitabilityScore: Option[Double],
    impactScore: Option[Double]
  )
  def main(args: Array[String]): Unit = {
    val filesPath = "src/main/resources/json_files"
    val outputDir = "./extracted_json_files" // Dossier pour les fichiers JSON extraits
    val outputFile = "./filtered_cves.json" // Fichier de sortie 

    val directory = new File(filesPath)
    if (!directory.exists || !directory.isDirectory) {
      println(s"Le chemin $filesPath n'est pas un dossier valide.")
      return
    }
    
    // Lister les fichiers .zip dans le dossier
    val zipFiles = directory.listFiles.filter(_.getName.endsWith(".zip"))
    if (zipFiles.isEmpty) {
      println("Aucun fichier .zip trouvé dans le dossier json_files.")
      return
    }

    // Parcourir les fichiers .zip et les extraire
    new File(outputDir).mkdirs() // Crée le répertoire si nécessaire
    zipFiles.foreach { zipFile =>
      println(s"Traitement du fichier : ${zipFile.getName}")

      val zipStream = new ZipInputStream(new FileInputStream(zipFile))
      var entry: ZipEntry = zipStream.getNextEntry

      while (entry != null) {
        if (!entry.isDirectory) {
          val outputFile = new File(outputDir, entry.getName)
          println(s"Extraction du fichier : ${outputFile.getName}")

          // Créer les dossiers nécessaires pour les fichiers extraits
          outputFile.getParentFile.mkdirs()

          val outStream = new FileOutputStream(outputFile)
          zipStream.transferTo(outStream)
          outStream.close()
        }
        entry = zipStream.getNextEntry
      }

      zipStream.close()
      println(s"Fichier ${zipFile.getName} traité avec succès.")
    }

    println("Tous les fichiers ont été extraits.")
  }
}