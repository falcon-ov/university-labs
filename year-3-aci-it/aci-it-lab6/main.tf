provider "google" {
  project = var.project_id
  region  = var.gcp_region
}

resource "google_compute_instance" "vm" {
  name         = "web-${var.env}"
  machine_type = "f1-micro"
  zone         = "${var.gcp_region}-a"

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-12"
    }
  }

  network_interface {
    network = "default"
    access_config {}
  }
}

resource "google_storage_bucket" "bucket" {
  name     = "my-simple-bucket-dev-dansoc7723"
  location = var.gcp_region
}
terraform {
  backend "gcs" {
    bucket = "my-tf-state-simple"  # имя бакета, который создал
    prefix = "terraform/state"      # путь внутри бакета, где будет храниться state
  }
}