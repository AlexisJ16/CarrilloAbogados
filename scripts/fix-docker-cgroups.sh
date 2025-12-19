#!/bin/bash
# Fix Docker cgroups issue in WSL2

# Create Docker daemon config to use cgroupfs
sudo mkdir -p /etc/docker
cat << 'EOF' | sudo tee /etc/docker/daemon.json
{
  "exec-opts": ["native.cgroupdriver=cgroupfs"]
}
EOF

# Restart Docker
sudo service docker restart
sleep 5

# Check Docker status
docker info | grep -i cgroup

echo "Docker reconfigured. Now starting Minikube..."
