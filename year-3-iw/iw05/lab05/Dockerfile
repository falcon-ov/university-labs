FROM jenkins/ssh-agent

# Install PHP-CLI and required dependencies
RUN apt-get update && apt-get install -y \
    php-cli \
    php-mbstring \
    php-xml \
    php-curl \
    unzip \
    wget \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install Composer
RUN wget https://getcomposer.org/installer -O composer-setup.php && \
    php composer-setup.php --install-dir=/usr/local/bin --filename=composer && \
    rm composer-setup.php

# Verify installations
RUN php --version && composer --version