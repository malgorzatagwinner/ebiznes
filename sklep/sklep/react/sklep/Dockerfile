from malgwinner/ebiznes as build
copy package.json yarn.lock ./
run ["npm", "install"]

copy src ./src/
copy public ./public/

run ["npm", "run", "build"]
expose 80

run ["npm", "install", "-g", "node-static"]
cmd ["static", "build", "-a", "0.0.0.0", "-p", "80"]
