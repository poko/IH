<?php
//include 'json_headers.php';

if (version_compare(PHP_VERSION, '5.1.0', '>=')) { date_default_timezone_set('UTC'); }

include 'db_open.php';

$query = sprintf("insert into acra_reports (version_name, package_name, file_path, phone_model, android_version, board, brand, device, build_display, fingerprint, host, id, model, product, tags, time, type, user, total_mem_size, available_mem_size, custom_data, stack_trace, initial_configuration, crash_configuration, display, user_comment, user_crash_date) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                  mysql_real_escape_string($_POST["entry_0_single"]),
                  mysql_real_escape_string($_POST["entry_1_single"]),
                  mysql_real_escape_string($_POST["entry_2_single"]),
                  mysql_real_escape_string($_POST["entry_3_single"]),
                  mysql_real_escape_string($_POST["entry_4_single"]),
                  mysql_real_escape_string($_POST["entry_5_single"]),
                  mysql_real_escape_string($_POST["entry_6_single"]),
                  mysql_real_escape_string($_POST["entry_7_single"]),
                  mysql_real_escape_string($_POST["entry_8_single"]),
                  mysql_real_escape_string($_POST["entry_9_single"]),
                  mysql_real_escape_string($_POST["entry_10_single"]),
                  mysql_real_escape_string($_POST["entry_11_single"]),
                  mysql_real_escape_string($_POST["entry_12_single"]),
                  mysql_real_escape_string($_POST["entry_13_single"]),
                  mysql_real_escape_string($_POST["entry_14_single"]),
                  mysql_real_escape_string($_POST["entry_15_single"]),
                  mysql_real_escape_string($_POST["entry_16_single"]),
                  mysql_real_escape_string($_POST["entry_17_single"]),
                  mysql_real_escape_string($_POST["entry_18_single"]),
                  mysql_real_escape_string($_POST["entry_19_single"]),
                  mysql_real_escape_string($_POST["entry_20_single"]),
                  mysql_real_escape_string($_POST["entry_21_single"]),
                  mysql_real_escape_string($_POST["entry_22_single"]),
                  mysql_real_escape_string($_POST["entry_23_single"]),
                  mysql_real_escape_string($_POST["entry_24_single"]),
                  mysql_real_escape_string($_POST["entry_25_single"]),
                  mysql_real_escape_string($_POST["entry_26_single"])
                  );
$result = mysql_query($query);

if (!$result) {
    $message  = 'Invalid query: ' . mysql_error() . "\n";
    $message .= 'Whole query: ' . $query;
    die($message);
}
include 'db_close.php';

echo "ecoarttech.net: ACRA Report Received";
?>
