package app.car.cap07.infrastructure

import org.springframework.core.io.ClassPathResource

fun loadFileContents(fileName: String, variables: Map<String,String> = emptyMap()): String {
    var fileContents = ClassPathResource(fileName).inputStream.readAllBytes().toString(Charsets.UTF_8)
    variables.forEach { key, value -> fileContents = fileContents.replace("{{$key}}", value) }
    return fileContents
}