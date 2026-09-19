# FTC Programming Setup Guide — New Member Onboarding (Windows)

Welcome to the team! This guide gets a brand-new programmer from "empty laptop" to
"built and ran an OpMode" using our **onboarding repo** — a separate, Pedro
Pathing-free repo built for learning, not the team's live competition code.

> **Note:** This is *not* the repo you'll use once you're fully ramped up. The real
> competition repo adds Pedro Pathing (for autonomous path-following) and other
> team-specific code once you're ready for it. This onboarding repo uses the exact
> same SDK version and the same driving code pattern, so nothing here gets "un-learned"
> later — it's a genuine subset, not a simplified fake version.

---

## 1. Install Git

Git is required to download (clone) the project and save your changes.

1. Go to <https://git-scm.com/download/win>. The download should start automatically.
2. Run the installer. Defaults are fine for everything except:
   - Make sure **Git Bash** is installed (recommended — it gives you a terminal that
     works the same way on every machine).
   - Select **"Use Git from the command line and also from 3rd-party software"** when
     prompted.
3. Verify it worked: open **Command Prompt** and type:
   ```
   git --version
   ```
   You should see a version number.

---

## 2. Install Android Studio

Android Studio is the IDE we use to write, build, and deploy robot code. It bundles
its own Java, so you do **not** need to separately install a JDK.

1. Go to <https://developer.android.com/studio> and download the Windows installer.
2. Run the installer with default options.
3. On first launch, install **Android SDK Platform API 34** (Android 14.0,
   "UpsideDownCake") — this matches what the project actually compiles against:
   - **File ▸ Settings ▸ Languages & Frameworks ▸ Android SDK**
   - Under the **SDK Platforms** tab, check **Android 14.0 (UpsideDownCake) / API 34**
   - Under the **SDK Tools** tab, make sure **Android SDK Build-Tools**,
     **Platform-Tools**, and **Command-line Tools (latest)** are checked
   - Click **Apply** and let it download

> It's fine if other API levels show up already installed — the build uses whichever
> `compileSdk` is set in the project (currently **34**), not whatever's newest on your
> machine.

---

## 3. Get a GitHub account

1. Create an account at <https://github.com/> if you don't have one already.
2. Ask your team lead or Noah to add you as a collaborator on the onboarding repo (if it's
   private), or skip this if it's public.

---

## 4. Clone the onboarding repo

1. Open **Command Prompt** or **Git Bash**.
2. Run:
   ```
   git clone https://github.com/noahetaylor/24171-Onboarding.git C:\projects\ftc-onboarding
   ```
   This downloads the code into `C:\projects\ftc-onboarding`. The first time you clone
   a private repo, it'll prompt you to log in to GitHub in your browser — follow that
   prompt.

---

## 5. Open the project in Android Studio

1. Launch Android Studio.
2. On the welcome screen (or **File ▸ Open** if it's already running), navigate to
   `C:\projects\ftc-onboarding` and select the folder.
3. Click **Trust Project** if prompted.
4. Wait for the first Gradle sync to finish — this can take **5–15 minutes** the first
   time, since it's downloading all the project's dependencies. You'll see progress in
   the status bar at the bottom. Don't close Android Studio while this is running.

---

## 6. Build the project

You don't need a physical robot to confirm your code compiles.

- In Android Studio: **Build ▸ Make Project** (or `Ctrl+F9`)
- Or from the terminal:
  ```
  ./gradlew assembleDebug
  ```

Success looks like `BUILD SUCCESSFUL`. That's your "hello world" — if you get here,
your environment is fully set up.

> This repo doesn't include a robot simulator, so at this stage you're confirming your
> code *compiles*, not watching it move. Actually running code on a robot happens on
> team hardware, at meetings, with a mentor present.

---

## 7. Understand the project layout

- **`TeamCode/`** — almost all of your code lives here, under
  `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.
- **`FtcRobotController/`** — the underlying FTC SDK, including official sample
  OpModes under `FtcRobotController/.../external/samples`. You generally won't edit
  this module directly, but it's worth browsing for examples.
- An **OpMode** is a Java class that defines one "program" the Driver Station can run
  — driver-controlled (`@TeleOp`) or fully scripted (`@Autonomous`).
- We use a **subsystem pattern** to keep hardware setup separate from driving logic:
  - `subsystems/RobotHardware.java` — owns the physical motor/servo references and
    does the one-time `hardwareMap` lookups. Every device name and starting direction
    lives in exactly one place.
  - `subsystems/Drivetrain.java` — takes a `RobotHardware` and exposes driving methods
    like `driveRobotCentric(forward, strafe, rotate)`. OpModes call these methods
    instead of touching motors directly.
  - `TeleOpTemplate.java` — a working example OpMode showing this pattern in use, from
    gamepad input to motor output.
  - `AutonomousTemplate.java` — a working example of a scripted (non-driver-controlled)
    OpMode using the same `Drivetrain`, driven by fixed timing instead of gamepad input.

  As you add more mechanisms (arms, intakes, etc.), each gets declared and
  initialized in `RobotHardware` and driven through its own subsystem class,
  following the same shape.

---

## 8. Look at the working example

Open `TeleOpTemplate.java`. It's short — read it start to finish before writing
anything yourself:

```java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.RobotHardware;

@TeleOp(name = "TeleOp Template")
public class TeleOpTemplate extends OpMode {

    private RobotHardware hardware;
    private Drivetrain drivetrain;

    @Override
    public void init() {
        hardware = new RobotHardware();
        hardware.init(hardwareMap);
        drivetrain = new Drivetrain(hardware);

        telemetry.addLine("TeleOp init complete.");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Gamepad's left_stick_y is inverted by convention (pushing the
        // stick forward/up returns a negative value), so it's negated here.
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        drivetrain.driveRobotCentric(forward, strafe, rotate);

        telemetry.addData("forward", forward);
        telemetry.addData("strafe", strafe);
        telemetry.addData("rotate", rotate);
        telemetry.update();
    }
}
```

Trace the flow: `init()` builds the hardware and drivetrain objects once. `loop()`
runs continuously (many times per second) while TeleOp is active — reading the
gamepad, handing the numbers to `drivetrain.driveRobotCentric()`, and printing them to
telemetry so you can see what's happening on the Driver Station.

---

## 9. Write your first OpMode

The easiest way to start is to copy an existing OpMode and modify it — this is also
how the team does it, so you're learning the real workflow, not a shortcut.

1. In Android Studio's Project view, find `TeleOpTemplate.java` under
   `TeamCode/java/org.firstinspires.ftc.teamcode`.
2. Right-click it ▸ **Copy**, paste it into the same folder, and give the new class a
   meaningful name (e.g. `MyFirstOpMode`) when prompted.
3. Every OpMode starts with an annotation and a name that controls what shows up on
   the Driver Station's OpMode list:
   ```java
   @TeleOp(name = "My First OpMode", group = "Practice")
   ```
   Change the string in `name = "..."` to whatever you want to see on the list.
4. To make an **autonomous** OpMode instead of a driver-controlled one:
   - Change the annotation and import from `@TeleOp` / `TeleOp` to `@Autonomous` /
     `Autonomous`:
     ```java
     import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
     // ...
     @Autonomous(name = "My First Autonomous", group = "Practice")
     ```
   - Delete the contents of `loop()` and use `init()` to run a fixed sequence instead
     — since `Drivetrain` already exists, a simple scripted move looks like:
     ```java
     drivetrain.driveRobotCentric(1, 0, 0); // full power, straight forward
     sleep(1000);                            // wait one second
     drivetrain.stop();
     ```

> There's already a working example of exactly this — `AutonomousTemplate.java`, right
> next to `TeleOpTemplate.java`. If you'd rather copy a finished file than build one
> from scratch, start there instead of following steps 1–4 above.

Sample OpModes with more advanced patterns (sensors, specific mechanisms) live in
`FtcRobotController/.../external/samples` — their names follow a `Basic-`, `Sensor-`,
`Robot-`, or `Concept-` prefix depending on what they demonstrate. Worth browsing once
you're comfortable with the basics above.

---

## 10. Save and share your changes

1. Stage your new/changed file:
   ```
   git add <path to your file>
   ```
2. Commit with a message describing what you did:
   ```
   git commit -m "Add MyFirstOpMode"
   ```
3. Push it up:
   ```
   git push
   ```

**Team convention:** don't commit straight to `master`/`main`. Create a branch for
your change and open a pull request instead — a mentor or lead will review it before
it merges. Ask the lead programmer to show you this the first time.

---

## 11. (Optional) Set up a visual diff/merge tool

This makes Git conflicts far less painful to resolve. [WinMerge](https://winmerge.org/downloads/)
is a good free option:

1. Download and run the installer from <https://winmerge.org/downloads/>.
2. Use default install options; on the **Select Additional Tasks** page, make sure
   **Enable Explorer context menu integration** and **Add WinMerge folder to your
   system path** are checked.
3. In Git Bash, run:
   ```bash
   git config --global diff.tool winmerge
   git config --global difftool.winmerge.cmd '"C:/Program Files/WinMerge/WinMergeU.exe" -u -e "$LOCAL" "$REMOTE"'
   git config --global difftool.prompt false

   git config --global merge.tool winmerge
   git config --global mergetool.winmerge.cmd '"C:/Program Files/WinMerge/WinMergeU.exe" -u -e -dl "Base" -dr "Mine" "$LOCAL" "$REMOTE" "$MERGED"'
   git config --global mergetool.prompt false
   git config --global mergetool.keepBackup false
   ```

---

## Questions?

Ask in the programming channel of the robotics Discord — everyone on this team started exactly where
you are now.
