package org.mbs.budgetplannerserver.domain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class YearTest {

    @Test
    public void shouldCalculateCurrentYearAccurately() {
        LocalDateTime january2021 = LocalDateTime.of(2021, 1, 1, 0, 0);
        assertThat(new Year().currentYearFor(january2021), is(2020));
    }

    private String extractFileName(String mediaURL, boolean strictCheck) throws Exception {
        String fileName = mediaURL.substring(mediaURL.lastIndexOf("/"));
        URL url = new URL(mediaURL);
        URLConnection con = url.openConnection();
        String contentDisposition = con.getHeaderField("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=\"")) {
            fileName = contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
        } else if(strictCheck) {
            throw new Exception(format("Can not extract file name from the URL '%s'. Make sure media download URL is correct.", mediaURL));
        }
        return fileName;
    }

    @Test
    public void asd() throws Exception {
        downloadMediaToFile("https://icons.iconarchive.com/icons/iconsmind/outline/64/Eci-Icon-icon.png");
    }

    String getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
        con.getInputStream();

        if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectUrl = con.getHeaderField("Location");
            return getFinalURL(redirectUrl);
        }
        return url;
    }

    private File downloadMediaToFile(String mediaURL) throws Exception {
        try {
            URLConnection connection = new URL(mediaURL).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream input = connection.getInputStream();

            String contentDisposition = connection.getHeaderField("Content-Disposition");
            String fileName = contentDisposition != null && contentDisposition.contains("filename=\"") ?
                    contentDisposition.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1") :
                    mediaURL.substring(mediaURL.lastIndexOf("/"));

            String newFileName = format("%s/imports/%s", "/Users/vinay/Downloads", UUID.randomUUID().toString().concat(format(".%s", FilenameUtils.getExtension(fileName))));
            System.out.println(newFileName);

            File file = new File(newFileName);
            FileUtils.copyInputStreamToFile(input, file);

            return file;
        } catch (IOException e) {
            String message = format("Error while downloading media '%s' ", mediaURL);
            throw new Exception(message);
        }
    }

    @Test
    public void asdf() {
        String basicQuery = "select * from\n" +
                "(select bje.job_execution_id execution_id,\n" +
                "       bje.status status,\n" +
                "       bje.exit_code exit_code,\n" +
                "       bje.create_time create_time,\n" +
                "       bje.start_time start_time,\n" +
                "       bje.end_time end_time,\n" +
                "       string_agg(case when bjep.key_name = 'uuid' then bjep.string_val else '' end::text, '') uuid,\n" +
                "       string_agg(case when bjep.key_name = 'fileName' then bjep.string_val else '' end::text, '') fileName,\n" +
                "       sum(case when bjep.key_name = 'noOfLines' then bjep.long_val else 0 end) noOfLines,\n" +
                "       string_agg(case when bjep.key_name = 's3Key' then bjep.string_val else '' end::text, '') s3Key,\n" +
                "       sum(case when bjep.key_name = 'userId' then bjep.long_val else 0 end) userId,\n" +
                "       string_agg(case when bjep.key_name = 'type' then bjep.string_val::text else '' end::text, '') job_type,\n" +
                "       max(case when bjep.key_name = 'startDate' then bjep.date_val::timestamp else null::timestamp end::timestamp) startDate,\n" +
                "       max(case when bjep.key_name = 'endDate' then bjep.date_val::timestamp else null::timestamp end::timestamp) endDate,\n" +
                "       string_agg(case when bjep.key_name = 'subjectTypeUUID' then bjep.string_val::text else '' end::text, '') subjectTypeUUID,\n" +
                "       string_agg(case when bjep.key_name = 'programUUID' then bjep.string_val::text else '' end::text, '') programUUID,\n" +
                "       string_agg(case when bjep.key_name = 'encounterTypeUUID' then bjep.string_val::text else '' end::text, '') encounterTypeUUID,\n" +
                "       string_agg(case when bjep.key_name = 'reportType' then bjep.string_val::text else '' end::text, '') reportType,\n" +
                "       max(bse.read_count) read_count,\n" +
                "       max(bse.write_count) write_count,\n" +
                "       max(bse.write_skip_count) write_skip_count\n" +
                "from batch_job_execution bje\n" +
                "left outer join  batch_job_execution_params bjep on bje.job_execution_id = bjep.job_execution_id\n" +
                "left outer join batch_step_execution bse on bje.job_execution_id = bse.job_execution_id\n" +
                "group by bje.job_execution_id, bje.status, bje.exit_code, bje.create_time, bje.start_time, bje.end_time\n" +
                "order by bje.create_time desc) jobs\n" +
                "where jobs.userId = :userId\n";
        System.out.println(basicQuery);
    }
}