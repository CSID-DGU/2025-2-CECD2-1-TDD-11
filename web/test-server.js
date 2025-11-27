// 개발 서버 테스트 스크립트
import { spawn } from 'child_process'

console.log('🚀 개발 서버 시작 중...\n')

const server = spawn('npm', ['run', 'dev'], {
  cwd: process.cwd(),
  shell: true,
  stdio: 'pipe'
})

let started = false

server.stdout.on('data', (data) => {
  const output = data.toString()
  console.log(output)
  
  if (output.includes('Local:') && !started) {
    started = true
    console.log('\n✅ 서버 정상 시작!')
    console.log('브라우저에서 위 URL을 열어보세요.\n')
    console.log('종료하려면 Ctrl+C를 누르세요.')
  }
})

server.stderr.on('data', (data) => {
  const error = data.toString()
  if (error.includes('Failed to resolve') || error.includes('Error')) {
    console.error('❌ 에러 발생:')
    console.error(error)
    process.exit(1)
  }
})

server.on('close', (code) => {
  console.log(`\n서버 종료 (코드: ${code})`)
  process.exit(code)
})
