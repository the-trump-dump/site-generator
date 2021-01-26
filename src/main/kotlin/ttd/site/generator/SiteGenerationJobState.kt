package ttd.site.generator

import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@JobScope
@Component
class SiteGenerationJobState {

    val buildDate = Instant.now()
    val latestYearMonth = AtomicReference<YearMonth>()

}