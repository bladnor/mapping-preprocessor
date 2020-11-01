import com.thebuzzmedia.exiftool.ExifToolBuilder
import com.thebuzzmedia.exiftool.Tag
import com.thebuzzmedia.exiftool.core.UnspecifiedTag
import java.io.File
import java.util.stream.Collectors
import kotlin.streams.asStream

val tagNames = listOf(
    UnspecifiedTag("FileName"),
    UnspecifiedTag("IrradianceYaw"),
    UnspecifiedTag("Yaw"),
    UnspecifiedTag("GPSLatitude"),
    UnspecifiedTag("GPSLongitude")
)



fun main(args: Array<String>) {
//    val image = Paths.get("/home/rb/projects/mapping-preprocessor/src/test/resources/ms/IMG_0201_1.tif").toFile()
//    val image =
//        Paths.get("/home/rb/pixgreenshare/CH-20280-StadionLachen#30-20200930/images/m210-ms-rededge/000/IMG_0199_5.tif")
//            .toFile()

    val file = File("points1.csv")

    val toList = File("/home/rb/pixgreenshare/CH-20280-StadionLachen#30-20200930/images/m210-ms-rededge/000")
        .walkTopDown()
        .asStream()
        .filter { it.isFile }
        .map {
            val tagsToValues = parse(it)
            tagNames
                .stream()
                .map { tagName -> tagsToValues[tagName] }
                .collect(Collectors.joining(","))
        }.collect(Collectors.toSet()).forEach { file.appendText("\n" + it) }

}

@Throws(Exception::class)
fun parse(image: File?): Map<Tag, String> {
    // ExifTool path must be defined as a system property (`exiftool.path`),
    // but path can be set using `withPath` method.
    try {
        ExifToolBuilder().build().use { exifTool ->
            return exifTool.getImageMeta(
                image
            )
        }
    } catch (ex: Exception) {
//        log.error(ex.message, ex)
        return emptyMap()
    }
}
