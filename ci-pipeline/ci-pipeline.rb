#!/usr/bin/ruby

require 'logger'
require 'yaml'

class Settings
  def initialize
    if File.exists? '.ci-status'
      @config = YAML::load_file '.ci-status'
    else
      reset
    end
  end

  def reset
    @config = {}
    @config['status'] = 'ready'
    @config['phase'] = '0'
    save_settings
  end

  def status
    @config['status']
  end

  def ready?
    status == 'ready'
  end

  def phase_number
    @config['phase'].to_i
  end

  def task
    @config['task']
  end

  def next_phase_number
    phase_number + 1
  end

  def next_phase
    @config['phase'] = (phase_number + 1).to_s
    save_settings
  end

  def save_settings
    File.open('.ci-status', 'w') { |f| f.write @config.to_yaml }
  end

  def set_state(phase_number, current_task, status)
    @config['phase'] = phase_number.to_s
    @config['task'] = current_task
    @config['status'] = status
    save_settings
  end

  def complete_phase
    @config.delete 'task'
    @config['status'] = 'ready'
    save_settings
  end

  def show
    puts YAML.dump(@config)
  end
end

class Task
  def self.tasks(phase_number)
    Dir["tasks/#{phase_number}-*"].map { |t| Task.new t }
  end

  def initialize(name)
    @name = name
  end

  def execute
    @success = system "(#{@name}) > logs/#{@name}.log"
    @return_code = $?
  end

  def name
    @name
  end

  def errors?
    !@success
  end

  def return_code
    @return_code
  end
end

class Phase
  def initialize(listener, settings, number)
    @listener = listener
    @settings = settings
    @number = number
  end

  def tasks
    Task.tasks @number
  end

  def has_tasks?
    !tasks.empty?
  end

  def number
    @number
  end

  def run
    tasks.each do |task|
      runTask(task, false)
    end
    @settings.complete_phase
  end

  def retry
    failed_task = @settings.task

    tasks.each do |task|
      if task.name < failed_task
        @listener.skipping_task task
      else
        runTask(task, failed_task == task.name)
      end
    end
    @settings.complete_phase
  end

  def runTask(task, retry_flag)
    @listener.start_task task
    @settings.set_state(@number, task.name, 'running')
    task.execute
    @settings.set_state(@number, task.name, task.errors? ? 'failed' : 'success')
    @listener.end_task task
    exit 1 if task.errors?
  end
end

class LoggerListener
  def initialize(logger)
    @logger = logger
  end

  def skipping_task(task)
    @logger.info "Skipping task #{task.name}"
  end

  def start_task(task)
    @logger.info "Running task #{task.name}"
  end

  def end_task(task)
    if task.errors?
      @logger.info "The task #{task.name} failed: returned code: #{task.return_code}"
    else
      @logger.info "The task #{task.name} successfully completed"
    end
  end
end

logger = Logger.new(STDOUT)
if ARGV.length > 0
  settings = Settings.new
  listener = LoggerListener.new(logger)

  case ARGV[0]
    when 'run'
      if settings.ready?
        phase = Phase.new(listener, settings, settings.next_phase_number)

        if phase.has_tasks?
          phase.run
        else
          logger.info " No tasks for #{phase.number} - pipeline completed"
        end
      else
        logger.error "The pipeline failed on a previous task and cannot be run."
        settings.show
        exit 1
      end
    when 'status'
      settings.show
    when 'reset'
      logger.info "Pipeline has been reset"
      settings.reset
    when 'retry'
      if settings.ready?
        logger.error "The pipeline is healthy and does not need to be recovered"
      else
        phase = Phase.new(listener, settings, settings.phase_number)

        if phase.has_tasks?
          phase.retry
        else
          logger.info "No tasks for #{phase.number} - pipeline completed"
        end
      end
    else
      logger.error "Unknown command #{ARGV[0]}"
      exit 1
  end
else
  logger.error "Argument expected"
  logger.info "  reset - resets the pipeline's state so that it can be re-run."
  logger.info "  retry - retries to run the pipeline from the previously failed task."
  logger.info "  run - runs the next phase of the pipeline.  If the pipeline previously failed then this command will itself fail."
  logger.info "  status - shows the status of the pipeline."
  exit 1
end
