output "vm_public_ip" {
  value = google_compute_instance.vm.network_interface[0].access_config[0].nat_ip
}

output "bucket_name" {
  value = google_storage_bucket.bucket.name
}
