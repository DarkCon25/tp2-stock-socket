const fs = require('fs');
const net = require('net');

const HOST = '127.0.0.1';
const PORT = 5000;

function loadItems() {
  const lines = fs.readFileSync('data.csv', 'utf8').trim().split(/\r?\n/);
  const items = new Map();
  for (const line of lines.slice(1)) {
    const [id, name, price, quantity] = line.split(',');
    items.set(Number(id), { name, price: Number(price), quantity });
  }
  return items;
}

function handle(command, items) {
  const parts = command.trim().split(/\s+/);
  if (parts.length === 2 && parts[0] === 'name') {
    return items.get(Number(parts[1]))?.name ?? 'Item not found';
  }
  if (parts.length === 2 && parts[0] === 'quantity') {
    return items.get(Number(parts[1]))?.quantity ?? 'Item not found';
  }
  if (parts.length === 3 && parts[0] === 'price') {
    const item = items.get(Number(parts[1]));
    const qty = Number(parts[2]);
    if (!item) return 'Item not found';
    if (!Number.isFinite(qty)) return 'Invalid command';
    return (item.price * qty).toFixed(2);
  }
  if (parts.length === 1 && parts[0] === 'end') return 'Goodbye';
  return 'Invalid command';
}

const items = loadItems();
net.createServer((socket) => {
  console.log('Client connected');
  let buffer = '';
  socket.setEncoding('utf8');
  socket.on('data', (chunk) => {
    buffer += chunk;
    let newline;
    while ((newline = buffer.indexOf('\n')) !== -1) {
      const command = buffer.slice(0, newline).replace(/\r$/, '');
      buffer = buffer.slice(newline + 1);
      socket.write(handle(command, items) + '\n');
      if (command.trim() === 'end') socket.end();
    }
  });
}).listen(PORT, HOST, () => console.log(`Node.js server listening on ${HOST}:${PORT}`));
