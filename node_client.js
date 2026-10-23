const net = require('net');
const readline = require('readline');

const socket = net.createConnection({ host: '127.0.0.1', port: 5000 }, () => {
  console.log('Connected to server');
  console.log('Request (name id / quantity id / price id qty / end):');
});
const terminal = readline.createInterface({ input: process.stdin, output: process.stdout });
let responseBuffer = '';
let lastCommand = '';
let waitingForResponse = false;
const requests = [];

terminal.on('line', (command) => {
  requests.push(command.trim());
  sendNext();
});

function sendNext() {
  if (!waitingForResponse && requests.length > 0) {
    lastCommand = requests.shift();
    waitingForResponse = true;
    socket.write(lastCommand + '\n');
  }
}

socket.setEncoding('utf8');
socket.on('data', (chunk) => {
  responseBuffer += chunk;
  const newline = responseBuffer.indexOf('\n');
  if (newline !== -1) {
    console.log('Server response:', responseBuffer.slice(0, newline));
    responseBuffer = responseBuffer.slice(newline + 1);
    if (lastCommand === 'end') {
      terminal.close();
      socket.end();
    } else {
      waitingForResponse = false;
      sendNext();
    }
  }
});
socket.on('error', (error) => { console.error(error.message); terminal.close(); });
